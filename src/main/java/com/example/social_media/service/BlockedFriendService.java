package com.example.social_media.service;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.social_media.entity.BlockedFriend;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.BlockedFriendRepository;

import jakarta.transaction.Transactional;

@Service
public class BlockedFriendService {

    private final BlockedFriendRepository repository;
    private final UserInfoService userInfoService;

    @Autowired
    public BlockedFriendService(BlockedFriendRepository repository, UserInfoService userInfoService){
        this.repository = repository;
        this.userInfoService = userInfoService;
    }

    @Transactional
    public BlockedFriend blockFriend(Long blockedId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo blocker = userInfoService.findByEmail(auth.getName());
        UserInfo blocked = userInfoService.findById(blockedId);
        if (blocker.getId().equals(blockedId)) {
            throw new IllegalStateException("You cannot block yourself");
        }
        boolean exists = repository.existsByBlockerAndBlocked(blocker, blocked);

        if(exists){
            throw new IllegalStateException("Users are already blocked!");
        }else{
            userInfoService.removeFriend(blocker, blocked);
            BlockedFriend blockedFriend = new BlockedFriend();
            blockedFriend.setBlocker(blocker);
            blockedFriend.setBlocked(blocked);
            return repository.save(blockedFriend);
        }
    }

    @Transactional
    public BlockedFriend unblockFriend(Long blockedId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo blocker = userInfoService.findByEmail(auth.getName());
        UserInfo blocked = userInfoService.findById(blockedId);
        if (blocker.getId().equals(blockedId)) {
            throw new IllegalStateException("You cannot unblock yourself");
        }
        BlockedFriend blockedFriend = repository.findByBlockerAndBlocked(blocker, blocked);
        if(blockedFriend==null){
            throw new IllegalStateException("Users are not blocked!");
        }else {
            repository.delete(blockedFriend);
            return blockedFriend;
        }
    }

    public List<Long> getBlockedFriendIds() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo blocker = userInfoService.findByEmail(auth.getName());
        return repository.findByBlocker(blocker)
                .stream()
                .map(bf -> bf.getBlocked().getId())
                .toList();
    }
}

package com.example.social_media.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.enums.FriendRequestStatus;
import com.example.social_media.entity.FriendRequest;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.BlockedFriendRepository;
import com.example.social_media.repository.FriendRequestRepository;

import jakarta.transaction.Transactional;

@Service
public class FriendRequestService {
    private final FriendRequestRepository repository;
    private final BlockedFriendRepository blockedFriendRepository;
    private final UserInfoService userInfoService;

    @Autowired
    public FriendRequestService(FriendRequestRepository repository, UserInfoService userInfoService, BlockedFriendRepository blockedFriendRepository){
        this.repository = repository;
        this.userInfoService = userInfoService;
        this.blockedFriendRepository = blockedFriendRepository;
    }

    @Transactional
    public FriendRequest sendFriendRequest(Long receiverId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo sender = userInfoService.findByEmail(auth.getName());
        if (sender.getId().equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        UserInfo receiver = userInfoService.findById(receiverId);
        boolean exists = repository.existsBySenderAndReceiver(sender, receiver) || repository.existsBySenderAndReceiver(receiver, sender);
        boolean isBlocked = blockedFriendRepository.existsByBlockerAndBlocked(sender, receiver) || blockedFriendRepository.existsByBlockerAndBlocked(receiver, sender);
        if(isBlocked){
            throw new IllegalStateException("Friends are blocked"); 
        }
        if(exists){
            throw new IllegalStateException("Friend request already exists between users");
        }else{
            FriendRequest request = new FriendRequest();
            request.setSender(sender);
            request.setReceiver(receiver);
            //request.setStatus(FriendRequestStatus.PENDING);
            return repository.save(request);
        }
    }

    @Transactional
    public FriendRequest acceptFriendRequest(Long senderId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo receiver = userInfoService.findByEmail(auth.getName());
        UserInfo sender = userInfoService.findById(senderId);

        FriendRequest request = repository
        .findBySenderAndReceiver(sender, receiver)
        .orElseThrow(() ->
                new IllegalStateException("Friend request does not exist"));
        /*if(request.getStatus() != FriendRequestStatus.PENDING){
            throw new IllegalStateException("Friend request is not pending");
        }*/
        userInfoService.addFriend(sender, receiver);
        repository.delete(request);
        return request;
    }

}

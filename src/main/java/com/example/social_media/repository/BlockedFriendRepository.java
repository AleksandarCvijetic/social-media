package com.example.social_media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.BlockedFriend;
import com.example.social_media.entity.UserInfo;

@Repository
public interface BlockedFriendRepository extends JpaRepository<BlockedFriend, Long>{
    boolean existsByBlockerAndBlocked(UserInfo blocker, UserInfo blocked);
    boolean existsByBlockerAndBlocked_Id(UserInfo blocker, Long blockedId);
    List<BlockedFriend> findByBlocker(UserInfo blocker);
    BlockedFriend findByBlockerAndBlocked(UserInfo blocker, UserInfo blocked);
}

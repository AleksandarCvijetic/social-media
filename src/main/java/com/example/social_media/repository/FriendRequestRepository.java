package com.example.social_media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.FriendRequest;
import com.example.social_media.entity.UserInfo;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long>{
    Optional<FriendRequest> findBySenderAndReceiver(UserInfo sender, UserInfo receiver);
    boolean existsBySenderAndReceiver(UserInfo sender, UserInfo receiver);
}

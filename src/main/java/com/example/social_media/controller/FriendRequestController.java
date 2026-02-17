package com.example.social_media.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.FriendRequestDto;
import com.example.dto.FriendRequestResponse;
import com.example.dto.UserInfoDto;
import com.example.social_media.entity.FriendRequest;
import com.example.social_media.service.FriendRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService service;

    @PostMapping("/{receiverId}/sendFriendRequest")
    public String sendFriendRequest(@PathVariable Long receiverId){
        return service.sendFriendRequest(receiverId);
    }

    @PostMapping("/{senderId}/acceptFriendRequest")
    public FriendRequestResponse acceptFriendRequest(@PathVariable Long senderId){
        FriendRequest request = service.acceptFriendRequest(senderId);

        return new FriendRequestResponse(
            request.getId(),
            request.getSender().getId(),
            request.getSender().getEmail(),
            request.getReceiver().getId()
        );
    }

    @PostMapping("/{senderId}/rejectFriendRequest")
    public String rejectFriendRequest(@PathVariable Long senderId){
        return service.rejectFriendRequest(senderId);
    }

    @GetMapping("/myRequests")
    public List<FriendRequestDto> getMyRequests() {
        return service.getPendingRequestsForUser();
    }

    @GetMapping("/myFriends")
    public List<UserInfoDto> getMyFriends() {
        return service.getAllFriends();
    }
}

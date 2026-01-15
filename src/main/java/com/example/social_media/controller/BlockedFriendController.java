package com.example.social_media.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_media.entity.BlockedFriend;
import com.example.social_media.service.BlockedFriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockedFriendController {
    private final BlockedFriendService service;

    @PostMapping("/{blockedId}/block")
    public BlockedFriend blockFriend(@PathVariable Long blockedId){
        return service.blockFriend(blockedId);
    }

    @PostMapping("/{blockedId}/unblock")
    public BlockedFriend unblockFriend(@PathVariable Long blockedId){
        return service.unblockFriend(blockedId);
    }
}

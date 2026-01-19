package com.example.dto;


public record FriendRequestResponse(
        Long id,
        Long senderId,
        String senderEmail,
        Long receiverId
) {}


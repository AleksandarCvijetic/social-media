package com.example.social_media.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.LikeDto;
import com.example.social_media.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeDto> toggleLike(@PathVariable Long postId){
        boolean liked = likeService.toggleLike(postId);
        return ResponseEntity.ok(new LikeDto(liked));
    }

    @GetMapping("/{postId}/likeCount")
    public Long getLikeCountForPost(@PathVariable Long postId){
        return likeService.getLikeCountForPost(postId);
    }
}

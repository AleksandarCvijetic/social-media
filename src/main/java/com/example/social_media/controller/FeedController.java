package com.example.social_media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.PostDto;
import com.example.social_media.entity.Post;
import com.example.social_media.service.FeedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/getFeed")
    public List<PostDto> getFeed(){
        return feedService.getFeedPosts();
    }

}

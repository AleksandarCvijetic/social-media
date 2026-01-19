package com.example.social_media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.PostDto;
import com.example.social_media.entity.Post;
import com.example.social_media.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService service;

    @GetMapping("/posts")
    public List<PostDto> getAllPosts(){
        return service.findAll();
    }

    @PostMapping("/addNewPost")
    public String addNewPost(@RequestBody Post post){
        return service.addPost(post);
    }

    @GetMapping("/getMyPosts")
    public List<PostDto> getMyPosts(){
        return service.findMyPosts();
    }

    @GetMapping("/getOtherPosts")
    public List<PostDto> getOtherPosts(){
        return service.findOtherPosts();
    }

    @GetMapping("/getFriendsPosts")
    public List<PostDto> getFriendsPosts(){
        return service.findFriendsPosts();
    }

    @GetMapping("/getFriendsPostsLastDay")
    public List<PostDto> getFriendsPostsLastDay(){
        return service.findFriendsPostsLastDay();
    }
    
}

package com.example.social_media.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.social_media.entity.Post;
import com.example.social_media.entity.PostReport;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.PostReportRepository;

@Service
public class PostReportService {
    private final PostReportRepository postReportRepository;
    private final PostService postService;
    private final UserInfoService userInfoService;
    @Autowired
    public PostReportService(PostReportRepository postReportRepository, PostService postService, UserInfoService userInfoService){
        this.postReportRepository = postReportRepository;
        this.postService = postService;
        this.userInfoService = userInfoService;
    }

    public UserInfo getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return user;  
    }

    public PostReport reportPost(PostReport report, int postId){
        Post post = postService.findById(postId);
        UserInfo reporter = getCurrentUser();

        if(post.getUser().getId().equals(reporter.getId())){
            throw new IllegalStateException("You cannot report your own post");
        }
        if(postReportRepository.existsByReportedPostAndReporter(post, reporter)){
            throw new IllegalStateException("You already reported this post");
        }

        report.setReportedPost(post);
        report.setReporter(reporter);
        report.setDateTime(LocalDateTime.now());

        return postReportRepository.save(report);
    }
}

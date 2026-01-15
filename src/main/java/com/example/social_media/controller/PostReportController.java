package com.example.social_media.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_media.entity.PostReport;
import com.example.social_media.service.PostReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class PostReportController {
    private final PostReportService postReportService;

    @PostMapping("/{postId}/report")
    public PostReport reportPost(@RequestBody PostReport postReport, @PathVariable int postId){
        return postReportService.reportPost(postReport, postId);
    }
}

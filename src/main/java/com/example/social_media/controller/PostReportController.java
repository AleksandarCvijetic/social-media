package com.example.social_media.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Void> reportPost(@RequestBody PostReport postReport,
                                        @PathVariable int postId) {
        postReportService.reportPost(postReport, postId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getReportCount(@PathVariable int postId) {
        return ResponseEntity.ok(
            postReportService.getReportCountForPost(postId)
        );
    }

}

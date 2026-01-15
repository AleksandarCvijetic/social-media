package com.example.social_media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ReviewDto;
import com.example.social_media.entity.Review;
import com.example.social_media.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{placeId}/review")
    public ReviewDto reviewPlace(@RequestBody Review review, @PathVariable Long placeId){
        return reviewService.addReview(review, placeId);
    }

    @GetMapping("/getAll")
    public List<Review> getAll(){
        return reviewService.findAll();
    }
    
}

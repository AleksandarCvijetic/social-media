package com.example.social_media.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.ReviewDto;
import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Place;
import com.example.social_media.entity.Review;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.HashtagRepository;
import com.example.social_media.repository.PlaceRepository;
import com.example.social_media.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private HashtagRepository hashtagRepository;

    public ReviewDto addReview(Review review, Long placeId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());

        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new RuntimeException("Place not found"));
        Set<Hashtag> resolvedHashtags = new HashSet<>();

        for(Hashtag h : review.getHashtags()){
            if(h.getName()==null || h.getName().isBlank()){
                throw new RuntimeException("Hashtag name cannot be null");
            }
            Hashtag hashtag = hashtagRepository
                .findByName(h.getName())
                .orElseGet(() -> hashtagRepository.save(h));

            resolvedHashtags.add(hashtag);
        }
        
        review.setHashtags(resolvedHashtags);
        review.setPlace(place);
        review.setUser(user);
        reviewRepository.save(review);
        ReviewDto dto = new ReviewDto(place.getId(), user.getId(), review.getGrade(), review.getDescription());
        return dto;
    }

    public List<Review> findAll(){
        return reviewRepository.findAll();
    }

    public ReviewDto findById(int id){
        Review review = reviewRepository.findById(id);
        ReviewDto dto = new ReviewDto(review.getPlace().getId(), review.getUser().getId(), review.getGrade(),review.getDescription());
        return dto;
    }

}

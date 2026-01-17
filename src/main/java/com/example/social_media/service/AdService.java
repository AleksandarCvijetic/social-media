package com.example.social_media.service;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Like;
import com.example.social_media.entity.Place;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.Review;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.HashtagRepository;
import com.example.social_media.repository.LikeRepository;
import com.example.social_media.repository.PlaceRepository;
import com.example.social_media.repository.PostRepository;
import com.example.social_media.repository.ReviewRepository;

@Service
public class AdService {
    private KieContainer kieContainer;
    private UserInfoService userInfoService;
    private PostRepository postRepository;
    private ReviewRepository reviewRepository;
    private PlaceRepository placeRepository;
    private LikeRepository likeRepository;
    private HashtagRepository hashtagRepository;

    public AdService(
        KieContainer kieContainer,
        UserInfoService userInfoService,
        PostRepository postRepository,
        ReviewRepository reviewRepository,
        PlaceRepository placeRepository,
        LikeRepository likeRepository,
        HashtagRepository hashtagRepository
    ){
        this.kieContainer = kieContainer;
        this.userInfoService = userInfoService;
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.placeRepository = placeRepository;
        this.likeRepository = likeRepository;
        this.hashtagRepository = hashtagRepository;
    }

    public List<Place> getRecommendedAds(){
        KieSession kieSession = kieContainer.newKieSession();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());

        for (Place p : placeRepository.findAll()) {
            kieSession.insert(p);
        }
        for (Review r : reviewRepository.findAll()) {
            kieSession.insert(r);
        }
        for (Hashtag h : hashtagRepository.findAll()) {
            kieSession.insert(h);
        }
        for (Like l : likeRepository.findAll()) {
            kieSession.insert(l);
        }
        for (Post p : postRepository.findAll()) {
            kieSession.insert(p);
        }
        kieSession.insert(user);

        List<Place> result = new ArrayList<>();
        for(Place place : placeRepository.findAll()){
            QueryResults qr = kieSession.getQueryResults(
                "recommendPlace", user, place);

            for (QueryResultsRow row : qr) {
                Place p = (Place) row.get("place");
                result.add(p);
            }
        }

        kieSession.dispose();
        return result;
    }
}

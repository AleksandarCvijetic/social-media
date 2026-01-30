package com.example.social_media.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.PlaceScore;
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
    private KieContainer adsKieContainer;
    private UserInfoService userInfoService;
    private PostRepository postRepository;
    private ReviewRepository reviewRepository;
    private PlaceRepository placeRepository;
    private LikeRepository likeRepository;
    private HashtagRepository hashtagRepository;

    public AdService(
        KieContainer adsKieContainer,
        UserInfoService userInfoService,
        PostRepository postRepository,
        ReviewRepository reviewRepository,
        PlaceRepository placeRepository,
        LikeRepository likeRepository,
        HashtagRepository hashtagRepository
    ){
        this.adsKieContainer = adsKieContainer;
        this.userInfoService = userInfoService;
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.placeRepository = placeRepository;
        this.likeRepository = likeRepository;
        this.hashtagRepository = hashtagRepository;
    }

    public List<Place> getRecommendedAds(){
        KieSession kieSession = adsKieContainer.newKieSession();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());

        for (Place p : placeRepository.findAll()) {
            kieSession.insert(p);
            kieSession.insert(new PlaceScore(p));
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
        Map<Place, Integer> scores = new HashMap<>();
        List<Place> result = new ArrayList<>();
        for(Place place : placeRepository.findAll()){
             // ne preporucuj vec review-ovana mesta
            if (queryCount(kieSession, "userAlreadyReviewedPlace", user, place) > 0) {
                continue;
            }

            int score = 0;

            score += queryCount(kieSession, "isUserFromPlaceCity", user, place);
            score += queryCount(kieSession, "userLikesSimillarPlace", user, place);
            score += queryCount(kieSession, "placeMatchesReviewHashtag", user, place);
            score += queryCount(kieSession, "placeMatchesLikedPostHashtag", user, place);
            score += queryCount(kieSession, "placeMatchesUserHashtagInPost", user, place);

            if (score > 0) {
                scores.put(place, score);
            }
            }

        kieSession.dispose();
        System.out.println(scores);
        return scores.entrySet()
            .stream()
            .sorted(Map.Entry.<Place, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();
    }

    private int queryCount(KieSession kieSession, String queryName, Object... args) {
        return kieSession.getQueryResults(queryName, args).size();
    }

}

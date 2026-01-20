package com.example.social_media.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Set;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.UserSimilarity;
import com.example.social_media.entity.Like;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.LikeRepository;
import com.example.social_media.repository.PostRepository;

@Service
public class FeedService {

    private KieContainer feedKieContainer;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private UserInfoService userInfoService;

    public FeedService(
        KieContainer feedKieContainer, 
        PostRepository postRepository, 
        LikeRepository likeRepository, 
        UserInfoService userInfoService
    ){
        this.feedKieContainer = feedKieContainer;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userInfoService = userInfoService;
    }

    public List<Post> getFeedPosts(){
        KieSession kieSession = feedKieContainer.newKieSession();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        List<Post> allPosts = postRepository.findAllByUserIdNot(user.getId());
        List<Like> allLikes = likeRepository.findAll();

        boolean isNewUser = postRepository.countByUserId(user.getId()) == 0
                && user.getFriends().isEmpty();
        System.out.println("DA LI JE NOV KORISNIK" + isNewUser);
        FeedRequest feedRequest = new FeedRequest(user, allPosts, isNewUser);

        kieSession.setGlobal("feedRequest", feedRequest);

        for(Post post : allPosts){
            kieSession.insert(post);
        }
        for(Like like : allLikes){
            kieSession.insert(like);
        }

        Map<Long, Set<Long>> likesByUser = new HashMap<>();

        for (Like like : allLikes) {
            likesByUser
                .computeIfAbsent(like.getUserId(), k -> new HashSet<>())
                .add(like.getPost().getId());
        }

        if (isNewUser) {
            Set<Long> likesA = likesByUser.getOrDefault(user.getId(), Set.of());

            for (Map.Entry<Long, Set<Long>> entry : likesByUser.entrySet()) {
                Long userU = entry.getKey();

                if (userU.equals(user.getId())) continue;

                Set<Long> likesU = entry.getValue();

                double pearson = calculatePearson(likesA, likesU);
                System.out.println("KOLIKI JE PEARSON:  " + pearson);
                if (pearson >= 0.5) {
                    kieSession.insert(
                        new UserSimilarity(user.getId(), userU, pearson)
                    );
                }
            }
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        return feedRequest.getRecommendedPosts();
    }

    private boolean isNewUser(UserInfo user) {
        boolean hasFriends = !user.getFriends().isEmpty();
        boolean hasPosts = !postRepository.findAllByUserId(user.getId()).isEmpty();
        return !hasFriends && !hasPosts;
    }

    private double calculatePearson(Set<Long> likesA, Set<Long> likesU) {
        System.out.println(likesA);
        System.out.println(likesU);
        // skup objava = unija lajkova
        Set<Long> allPosts = new HashSet<>();
        allPosts.addAll(likesA);
        allPosts.addAll(likesU);

        int n = allPosts.size();
        if (n < 2) return 0.0;
        System.out.println("N JE: " + n);
        double sumA = 0;
        double sumU = 0;

        for (Long postId : allPosts) {
            sumA += likesA.contains(postId) ? 1 : 0;
            sumU += likesU.contains(postId) ? 1 : 0;
        }
        System.out.println("SUM A JE: " + sumA);
        System.out.println("SUM B JE: " + sumU);


        double meanA = sumA / n;
        double meanU = sumU / n;

        double numerator = 0;
        double denomA = 0;
        double denomU = 0;

        for (Long postId : allPosts) {
            double rA = (likesA.contains(postId) ? 1 : 0) - meanA;
            double rU = (likesU.contains(postId) ? 1 : 0) - meanU;

            numerator += rA * rU;
            denomA += rA * rA;
            denomU += rU * rU;
        }
        System.out.println("DENOM A JE: " + denomA);
        System.out.println("DENOM B JE: " + denomU);        
        System.out.println("NUMERATRO JE: " + numerator);
        if (denomA == 0 || denomU == 0) return 0.0;

        return numerator / Math.sqrt(denomA * denomU);
    }



}

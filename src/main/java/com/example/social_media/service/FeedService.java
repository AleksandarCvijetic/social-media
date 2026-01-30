package com.example.social_media.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.PostDto;
import com.example.dto.PostSimilarity;
import com.example.dto.UserSimilarity;
import com.example.social_media.entity.Like;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.LikeRepository;
import com.example.social_media.repository.PostRepository;
import com.example.social_media.mapper.PostMapper;

@Service
public class FeedService {

    private KieContainer feedKieContainer;
    private KieContainer newUserFeedKieContainer;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private UserInfoService userInfoService;
    private PostMapper mapper;
    private PostService postService;

    public FeedService(
        KieContainer feedKieContainer, 
        PostRepository postRepository, 
        LikeRepository likeRepository, 
        UserInfoService userInfoService,
        KieContainer newUserFeedKieContainer,
        PostMapper mapper,
        PostService postService
    ){
        this.feedKieContainer = feedKieContainer;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userInfoService = userInfoService;
        this.newUserFeedKieContainer = newUserFeedKieContainer;
        this.mapper = mapper;
        this.postService = postService;
    }

    public List<PostDto> getFeedPosts(){
        //KieSession kieSession = feedKieContainer.newKieSession();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        List<Long> friendIds = new ArrayList<>(
            user.getFriends()
                .stream()
                .map(UserInfo::getId)
                .toList()
        );
        List<Post> myPosts = postRepository.findAllByUserId(user.getId());
        List<Post> allPosts = new ArrayList<>();


        if(friendIds.isEmpty()){
            allPosts = postService.findAllByUserIdNot(user.getId());
        }else{
            allPosts = postService.findAllByUserIdNotIn(friendIds);
        }
        if (allPosts.isEmpty()) {
            return List.of(); // prazan feed, 200 OK
        }

        List<Like> allLikes = likeRepository.findAll();
        boolean isNewUser = postRepository.countByUserId(user.getId()) == 0
                && user.getFriends().isEmpty();
        
        KieSession kieSession = isNewUser ? newUserFeedKieContainer.newKieSession() : feedKieContainer.newKieSession();
        System.out.println("DA LI JE NOV KORISNIK" + isNewUser);
        FeedRequest feedRequest = new FeedRequest(user, allPosts, isNewUser);

        kieSession.setGlobal("feedRequest", feedRequest);

        for(Post post : allPosts){
            kieSession.insert(post);
        }
        for (Post post : myPosts) {
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
        Map<Long, Set<Long>> usersByLikedPost = new HashMap<>();
        for (Like like : allLikes){
            usersByLikedPost.computeIfAbsent(like.getPost().getId(), k -> new HashSet<>()).add(like.getUserId());
        }
        Set<Long> likedByUser = allLikes.stream()
            .filter(l -> l.getUserId().equals(user.getId()))
            .map(l -> l.getPost().getId())
            .collect(Collectors.toSet());

        if(isNewUser){
            for (Long likedPostId : likedByUser) {
            Set<Long> likersA = usersByLikedPost.getOrDefault(likedPostId, Set.of());
            System.out.println(likersA);
            for (Map.Entry<Long, Set<Long>> entry : usersByLikedPost.entrySet()) {
                Long candidatePostId = entry.getKey();

                if (likedByUser.contains(candidatePostId)) continue;

                Set<Long> likersB = entry.getValue();
                System.out.println(likersB);
                double similarity = calculatePostSimilarity(likersA, likersB);
                System.out.println("SIMILARITY: " + similarity);
                //if (similarity >= 0.7) {
                    kieSession.insert(
                        new PostSimilarity(likedPostId, candidatePostId, similarity)
                    );
                //}
            }
        }

        }
        if (isNewUser) {
            Set<Long> likesA = likesByUser.getOrDefault(user.getId(), Set.of());

            for (Map.Entry<Long, Set<Long>> entry : likesByUser.entrySet()) {
                Long userU = entry.getKey();

                if (userU.equals(user.getId())) continue;

                Set<Long> likesU = entry.getValue();

                double pearson = calculatePearson(likesA, likesU, allPosts);
                System.out.println("KOLIKI JE PEARSON:  " + pearson);
                //if (pearson >= 0.5) {
                    kieSession.insert(
                        new UserSimilarity(user.getId(), userU, pearson)
                    );
                //}
            }
        }

        kieSession.fireAllRules();
        kieSession.dispose();
        List<Long> likedPostIds = likeRepository
            .findAllByUserId(user.getId())
            .stream()
            .map(like -> like.getPost().getId())
            .toList();

        return feedRequest.getRecommendedPosts()
                .stream()
                .filter(post -> !likedPostIds.contains(post.getId()))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private double calculatePearson(Set<Long> likesA, Set<Long> likesU, List<Post> allPosts) {
        System.out.println(likesA);
        System.out.println(likesU);

        List<Long> allPostsIds = allPosts.stream()
            .map(Post::getId)
            .collect(Collectors.toList());

        int n = allPostsIds.size();
        if (n < 2) return 0.0;
        System.out.println("N JE: " + n);
        double sumA = 0;
        double sumU = 0;

        for (Long postId : allPostsIds) {
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

        for (Long postId : allPostsIds) {
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

    private double calculatePostSimilarity(Set<Long> a, Set<Long> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        if (b.size() < 2) return 0.0;
        Set<Long> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        //int minSize = Math.min(a.size(), b.size());
        return (double) intersection.size() / a.size();
    }


}

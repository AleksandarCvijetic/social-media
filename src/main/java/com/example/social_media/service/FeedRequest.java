package com.example.social_media.service;

import com.example.social_media.entity.Like;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedRequest {

    private UserInfo user;                // trenutno ulogovani korisnik
    private List<Post> allPosts;          // sve potencijalne objave koje se mogu preporučiti
    private Map<Post, Integer> postScores; // bodovanje po pravilima

    public FeedRequest(UserInfo user, List<Post> allPosts) {
        this.user = user;
        this.allPosts = allPosts;
        this.postScores = new HashMap<>();
    }

    // GETTERS
    public UserInfo getUser() {
        return user;
    }

    public List<Post> getAllPosts() {
        return allPosts;
    }

    public Map<Post, Integer> getPostScores() {
        return postScores;
    }

    public void addScore(Post post, int score) {
        postScores.put(post, postScores.getOrDefault(post, 0) + score);
    }

    public void addRecommendedPost(Post post, int score) {
        addScore(post, score);
    }

    // na kraju, vratimo listu sortiranih objava po bodovima
    public List<Post> getRecommendedPosts() {
        List<Post> recommended = new ArrayList<>(postScores.keySet());
        recommended.sort((p1, p2) -> {
            int scoreCompare = postScores.get(p2).compareTo(postScores.get(p1));
            if(scoreCompare != 0) return scoreCompare;
            return p2.getCreatedAt().compareTo(p1.getCreatedAt()); // novije idu prve
        });
        // uzmi top 20
        return recommended.size() > 20 ? recommended.subList(0, 20) : recommended;
    }

}


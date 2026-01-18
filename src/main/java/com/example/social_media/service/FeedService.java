package com.example.social_media.service;

import java.util.List;

import java.util.Map;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        FeedRequest feedRequest = new FeedRequest(user, allPosts);

        kieSession.setGlobal("feedRequest", feedRequest);

        for(Post post : allPosts){
            kieSession.insert(post);
        }
        for(Like like : allLikes){
            kieSession.insert(like);
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



}

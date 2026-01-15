package com.example.social_media.service;

import java.util.List;

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

    private KieContainer kieContainer;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private UserInfoService userInfoService;

    public FeedService(KieContainer kieContainer, PostRepository postRepository, LikeRepository likeRepository, UserInfoService userInfoService){
        this.kieContainer = kieContainer;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userInfoService = userInfoService;
    }

    public List<Post> getFeedPosts(){
        KieSession kieSession = kieContainer.newKieSession();

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

}

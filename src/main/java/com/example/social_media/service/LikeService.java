package com.example.social_media.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.social_media.entity.Like;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.LikeRepository;
import com.example.social_media.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserInfoService userInfoService;

    @Autowired
    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserInfoService userInfoService){
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userInfoService = userInfoService;
    }

    @Transactional
    public boolean toggleLike(Long postId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isLiked = likeRepository.existsByPostAndUserId(post, user.getId());
        Like like = new Like();
        if(isLiked){
            likeRepository.deleteByPostAndUserId(post, user.getId());
            return false;
        }else{
            like.setPost(post);
            like.setUserId(user.getId());
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);
            return true; //liked
        }
    }

    public Long getLikeCountForPost(Long postId){
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        return likeRepository.countByPost(post);
    }

    public List<Like> findAllUserLikes(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return likeRepository.findAllByUserId(user.getId());
    }
}

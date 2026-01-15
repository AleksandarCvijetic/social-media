package com.example.social_media.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.repository.HashtagRepository;
import com.example.social_media.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final UserInfoService userInfoService;
    private final LikeService likeService;

    @Autowired
    public PostService(PostRepository postRepository, UserInfoService userInfoService, HashtagRepository hashtagRepository, LikeService likeService){
        this.postRepository = postRepository;
        this.userInfoService = userInfoService;
        this.hashtagRepository = hashtagRepository;
        this.likeService = likeService;
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    public Post findById(int id){
        return postRepository.findById(id);
    }

    public String addPost(Post post){
        System.out.println("DA LI UDJES OVDE????");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("A OBDE???");
        // auth.getName() sada vraća email (subject tokena)
        String userEmail = auth.getName();
        UserInfo user = userInfoService.findByEmail(userEmail);
        System.out.println("Debug: trenutno korisnik je " + user.getId());

        post.setUserId(user.getId());
        post.setCreatedAt(LocalDateTime.now());

        Set<Hashtag> resolvedHashtags = new HashSet<>();

        for(Hashtag h : post.getHashtags()){
            if(h.getName()==null || h.getName().isBlank()){
                throw new RuntimeException("Hashtag name cannot be null");
            }
            Hashtag hashtag = hashtagRepository
                .findByName(h.getName())
                .orElseGet(() -> hashtagRepository.save(h));

            resolvedHashtags.add(hashtag);
        }
        
        post.setHashtags(resolvedHashtags);
        postRepository.save(post);
        return "Post created!"; 
    }

    public List<Post> findMyPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return postRepository.findAllByUserId(user.getId());
    }

    public List<Post> findOtherPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return postRepository.findAllByUserIdNot(user.getId());
    }

    public List<Post> findFriendsPosts(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findAllByUserIdIn(friendIds);
    }

    public List<Post> findFriendsPostsLastDay(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        LocalDate oneDayAgo = LocalDate.now().minusDays(1);

        return postRepository.findAllByUserIdInAndCreatedAtAfter(friendIds, oneDayAgo);
    }

    public List<Post> findNotFriendsPosts(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findAllByUserIdNotIn(friendIds);
    }

}

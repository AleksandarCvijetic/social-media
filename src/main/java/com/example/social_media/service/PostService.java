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

import com.example.dto.PostDto;
import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Post;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.mapper.PostMapper;
import com.example.social_media.repository.HashtagRepository;
import com.example.social_media.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final UserInfoService userInfoService;
    private final LikeService likeService;
    private final PostMapper postMapper;
    private final BlockedFriendService blockedFriendService;

    @Autowired
    public PostService(PostRepository postRepository, UserInfoService userInfoService, HashtagRepository hashtagRepository, LikeService likeService, PostMapper postMapper, BlockedFriendService blockedFriendService){
        this.postRepository = postRepository;
        this.userInfoService = userInfoService;
        this.hashtagRepository = hashtagRepository;
        this.likeService = likeService;
        this.postMapper = postMapper;
        this.blockedFriendService = blockedFriendService;
    }

    public List<PostDto> findAll(){
        return postRepository.findAll()
            .stream()
            .map(postMapper::toDto)
            .collect(Collectors.toList());
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

        post.setUser(user);
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

    public List<PostDto> findMyPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return postRepository.findAllByUserId(user.getId())
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> findOtherPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        List<Long> blockedIds = blockedFriendService.getBlockedFriendIds();
        return postRepository.findAllByUserIdNot(user.getId())
                .stream()
                .filter(post -> !blockedIds.contains(post.getUser().getId()))
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Post> findAllByUserIdNot(Long userId){
        List<Long> blockedIds = blockedFriendService.getBlockedFriendIds();
        return postRepository.findAllByUserIdNot(userId)
                .stream()
                .filter(post -> !blockedIds.contains(post.getUser().getId()))
                .collect(Collectors.toList());
    }

    public List<Post> findAllByUserIdNotIn(List<Long> friendIds){
        List<Long> blockedIds = blockedFriendService.getBlockedFriendIds();
        return postRepository.findAllByUserIdNotIn(friendIds)
                .stream()
                .filter(post -> !blockedIds.contains(post.getUser().getId()))
                .collect(Collectors.toList());
    }

    public List<PostDto> findFriendsPosts(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findAllByUserIdIn(friendIds)
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> findFriendsPostsLastDay(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        return postRepository.findAllByUserIdInAndCreatedAtAfter(friendIds, oneDayAgo)
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> findNotFriendsPosts(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        
        List<Long> friendIds = user.getFriends().stream().map(UserInfo::getId).toList();
        if (friendIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findAllByUserIdNotIn(friendIds)
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

}

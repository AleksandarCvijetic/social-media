package com.example.social_media.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByUserIdNot(Long userId);
    List<Post> findAllByUserIdIn(List<Long> userIds);
    List<Post> findAllByUserIdNotIn(List<Long> userIds);
    List<Post> findAllByUserIdInAndCreatedAtAfter(List<Long> userIds, LocalDate date);
    //Post findByHashtag(Hashtag hashtag);
    Post findById(int postId);
}

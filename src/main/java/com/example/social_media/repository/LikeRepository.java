package com.example.social_media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.Like;
import com.example.social_media.entity.Post;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{
    boolean existsByPostAndUserId(Post post, Long userId);
    void deleteByPostAndUserId(Post post, Long userId);
    long countByPost(Post post);
    List<Like> findAllByUserId(Long userId);
}

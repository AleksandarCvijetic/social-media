package com.example.social_media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.social_media.entity.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>{
    Optional<Hashtag> findByName(String name);
}

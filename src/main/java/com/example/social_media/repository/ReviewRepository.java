package com.example.social_media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    List<Review> findAll();
    Review findById(int id);
    List<Review> findByPlace_Id(Long placeId);
}

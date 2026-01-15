package com.example.social_media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.social_media.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>{
    List<Place> findAll();
    Place findById(int id);
}

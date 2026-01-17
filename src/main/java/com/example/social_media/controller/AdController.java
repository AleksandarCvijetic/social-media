package com.example.social_media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.PlaceDto;
import com.example.social_media.mapper.PlaceMapper;
import com.example.social_media.service.AdService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ads")
public class AdController {
    private final AdService adService;
    private final PlaceMapper mapper;

    public AdController(AdService adService, PlaceMapper placeMapper) {
        this.adService = adService;
        this.mapper = placeMapper;
    }

    @GetMapping
    public List<PlaceDto> getRecommendedAds(){
        return adService.getRecommendedAds()
            .stream()
            .map(mapper::toDto)
            .toList();
    }
}

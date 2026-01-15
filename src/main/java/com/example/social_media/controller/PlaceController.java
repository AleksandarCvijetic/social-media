package com.example.social_media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_media.entity.Place;
import com.example.social_media.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping("/addPlace")
    public String addPlace(@RequestBody Place place){
        return placeService.addPlace(place);
    }

    @GetMapping("/getAllPlaces")
    public List<Place> getAllPlaces(){
        return placeService.findAll();
    }
}

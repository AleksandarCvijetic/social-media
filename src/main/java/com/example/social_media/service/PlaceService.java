package com.example.social_media.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.PlaceDto;
import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Place;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.mapper.PlaceMapper;
import com.example.social_media.repository.HashtagRepository;
import com.example.social_media.repository.PlaceRepository;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final UserInfoService userInfoService;
    private final HashtagRepository hashtagRepository;
    private final PlaceMapper mapper;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, UserInfoService userInfoService, HashtagRepository hashtagRepository, PlaceMapper mapper){
        this.placeRepository = placeRepository;
        this.userInfoService = userInfoService;
        this.hashtagRepository = hashtagRepository;
        this.mapper = mapper;
    }

    public UserInfo getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = userInfoService.findByEmail(auth.getName());
        return user;
    }

    public List<PlaceDto> findAll(){
        return placeRepository.findAll()
                .stream()
                .map(mapper::toDto)  // Mapiramo svaki UserInfo u UserInfoDto
                .collect(Collectors.toList());
    }

    public Place findById(int id){
        return placeRepository.findById(id);
    }

    public String addPlace(Place place){
        Set<Hashtag> resolvedHashtags = new HashSet<>();

        for(Hashtag h : place.getHashtags()){
            if(h.getName()==null || h.getName().isBlank()){
                throw new RuntimeException("Hashtag name cannot be null");
            }
            Hashtag hashtag = hashtagRepository
                .findByName(h.getName())
                .orElseGet(() -> hashtagRepository.save(h));

            resolvedHashtags.add(hashtag);
        }
        
        place.setHashtags(resolvedHashtags);

        placeRepository.save(place);
        return "Place added succesfully!";
    }

    


}

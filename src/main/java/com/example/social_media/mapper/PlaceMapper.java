package com.example.social_media.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.dto.PlaceDto;
import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Place;

@Component
public class PlaceMapper {

    public PlaceDto toDto(Place place) {
        if (place == null) return null;

        PlaceDto dto = new PlaceDto();
        dto.setId(place.getId());
        dto.setName(place.getName());

        if (place.getAddress() != null) {
            dto.setCity(place.getAddress().getCity());
            dto.setCountry(place.getAddress().getCountry());
        }

        Set<String> hashtagNames = place.getHashtags()
            .stream()
            .map(Hashtag::getName)
            .collect(Collectors.toSet());

        dto.setHashtags(hashtagNames);

        return dto;
    }
}

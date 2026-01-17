package com.example.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {

    private Long id;
    private String name;

    private String city;
    private String country;

    private Set<String> hashtags;
}

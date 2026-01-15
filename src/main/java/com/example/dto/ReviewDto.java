package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDto {
    private Long placeId;
    private Long userId;
    private int grade;
    private String description;
}

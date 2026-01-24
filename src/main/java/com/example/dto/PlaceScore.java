package com.example.dto;

import com.example.social_media.entity.Place;

public class PlaceScore {
    private Place place;
    private int score;

    public PlaceScore(Place place){
        this.place = place;
        this.score = 0;
    }

    public void addScore(int s){
        score += s;
    }

    public int getScore(){
        return score;
    }
}

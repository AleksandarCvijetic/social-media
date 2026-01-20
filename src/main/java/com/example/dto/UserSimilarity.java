package com.example.dto;

public class UserSimilarity {
    private Long userA;
    private Long userB;
    private double correlation;

    public UserSimilarity(Long userA, Long userB, double correlation) {
        this.userA = userA;
        this.userB = userB;
        this.correlation = correlation;
    }

    public boolean isSimilar() {
        return correlation >= 0.5;
    }

    public Long getUserA(){
        return userA;
    }

    public Long getUserB(){
        return userB;
    }
    public double getCorrelation(){
        return correlation;
    }
}


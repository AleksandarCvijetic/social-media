package com.example.dto;

public class PostSimilarity {
    private Long postA;
    private Long postB;
    private double similarity;

    public PostSimilarity(Long likedPost, Long candidatePost, double similarity){
        this.postA = likedPost;
        this.postB = candidatePost;
        this.similarity = similarity;
    }

    public Long getPostA(){
        return postA;
    }
    public Long getPostB(){
        return postB;
    }
    public double similarity(){
        return similarity;
    }
}

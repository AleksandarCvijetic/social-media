package com.example.social_media.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.dto.PostDto;
import com.example.social_media.entity.Hashtag;
import com.example.social_media.entity.Post;

@Component
public class PostMapper {

    public PostDto toDto(Post post) {
        if (post == null) return null;

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setText(post.getText());
        dto.setUserId(post.getUserId());
        dto.setCreatedAt(post.getCreatedAt());

        Set<String> hashtagNames = post.getHashtags()
                .stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());
        dto.setHashtags(hashtagNames);

        return dto;
    }
}

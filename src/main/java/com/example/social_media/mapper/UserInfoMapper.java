package com.example.social_media.mapper;

import org.springframework.stereotype.Component;

import com.example.dto.UserInfoDto;
import com.example.social_media.entity.UserInfo;

@Component
public class UserInfoMapper {

    public UserInfoDto toDto(UserInfo user) {
        if (user == null) return null;

        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());

        if (user.getAddress() != null) {
            dto.setCity(user.getAddress().getCity());
            dto.setCountry(user.getAddress().getCountry());
            dto.setStreet(user.getAddress().getStreet());
        }

        return dto;
    }
}

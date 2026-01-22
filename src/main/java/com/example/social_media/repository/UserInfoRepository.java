package com.example.social_media.repository;


import com.example.social_media.entity.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email); // Use 'email' if that is the correct field for login
    Optional<UserInfo> findById(int id);
    List<UserInfo> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}

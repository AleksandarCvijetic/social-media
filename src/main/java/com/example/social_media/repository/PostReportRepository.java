package com.example.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_media.entity.Post;
import com.example.social_media.entity.PostReport;
import com.example.social_media.entity.UserInfo;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long>{
    boolean existsByReportedPostAndReporter(Post post, UserInfo reporter);
}

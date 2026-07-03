package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.FeaturedProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeaturedProjectRepository
        extends JpaRepository<FeaturedProject, Long> {

    List<FeaturedProject> findByResume_IdOrderByDisplayOrderAsc(Long resumeId);
}

package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.ProjectTechnology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTechnologyRepository
        extends JpaRepository<ProjectTechnology, Long> {

    List<ProjectTechnology> findByProject_IdOrderByDisplayOrderAsc(Long projectId);
}

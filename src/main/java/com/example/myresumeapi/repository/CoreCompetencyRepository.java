package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.CoreCompetency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoreCompetencyRepository
        extends JpaRepository<CoreCompetency, Long> {

    List<CoreCompetency> findByResume_IdOrderByDisplayOrderAsc(Long resumeId);
}

package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.ProfessionalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalHistoryRepository
        extends JpaRepository<ProfessionalHistory, Long> {

    List<ProfessionalHistory> findByResume_IdOrderByDisplayOrderAsc(Long resumeId);
}

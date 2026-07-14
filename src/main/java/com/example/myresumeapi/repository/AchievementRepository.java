package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository
        extends JpaRepository<Achievement, Long> {

    List<Achievement> findByProfessionalHistory_IdOrderByDisplayOrderAsc(Long professionalHistoryId);
}

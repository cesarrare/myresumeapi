package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.TechnicalSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicalSkillRepository
        extends JpaRepository<TechnicalSkill, Long> {

    List<TechnicalSkill> findByResume_IdOrderByDisplayOrderAsc(Long resumeId);
}

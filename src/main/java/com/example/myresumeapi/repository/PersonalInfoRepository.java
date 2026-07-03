package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalInfoRepository
        extends JpaRepository<PersonalInfo, Long> {

    Optional<PersonalInfo> findByResume_Id(Long resumeId);
}

package com.example.myresumeapi.repository;

import com.example.myresumeapi.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    List<Resume> findByUser_IdOrderByUpdatedAtDesc(Long userId);
}
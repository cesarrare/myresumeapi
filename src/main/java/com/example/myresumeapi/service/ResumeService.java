package com.example.myresumeapi.service;

import com.example.myresumeapi.dto.ResumeResponse;
import com.example.myresumeapi.dto.ResumeSaveRequest;
import com.example.myresumeapi.dto.ResumeSummaryResponse;
import com.example.myresumeapi.dto.ResumeUpdateRequest;

import java.util.List;

public interface ResumeService {

    ResumeResponse getResumeById(Long resumeId);

    List<ResumeSummaryResponse> getResumesByUserId(Long userId);

    ResumeResponse saveResume(ResumeSaveRequest request);

    ResumeResponse updateResume(ResumeUpdateRequest request);

}
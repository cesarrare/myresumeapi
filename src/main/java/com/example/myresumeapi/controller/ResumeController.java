package com.example.myresumeapi.controller;

import com.example.myresumeapi.dto.ResumeResponse;
import com.example.myresumeapi.dto.ResumeSaveRequest;
import com.example.myresumeapi.dto.ResumeSummaryResponse;
import com.example.myresumeapi.dto.ResumeUpdateRequest;
import com.example.myresumeapi.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResumeResponse saveResume(@RequestBody ResumeSaveRequest request) {
        if (request.getUserId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "userId is mandatory"
            );
        }

        return resumeService.saveResume(request);
    }

    @PutMapping
    public ResumeResponse updateResume(@RequestBody ResumeUpdateRequest request) {
        if (request.getResumeId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "resumeId is mandatory"
            );
        }

        if (request.getUserId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "userId is mandatory"
            );
        }

        return resumeService.updateResume(request);
    }

    @GetMapping("/user/{userId}")
    public List<ResumeSummaryResponse> getResumesByUserId(
            @PathVariable Long userId
    ) {
        return resumeService.getResumesByUserId(userId);
    }

    @GetMapping("/{resumeId}")
    public ResumeResponse getResumeById(
            @PathVariable Long resumeId
    ) {
        return resumeService.getResumeById(resumeId);
    }
}

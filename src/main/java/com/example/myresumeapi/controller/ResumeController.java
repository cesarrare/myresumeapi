package com.example.myresumeapi.controller;

import com.example.myresumeapi.dto.ResumeBulkDeleteRequest;
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

import java.security.Principal;
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

    @DeleteMapping("/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResumes(
            @RequestBody ResumeBulkDeleteRequest request,
            Principal principal
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body is mandatory"
            );
        }

        resumeService.deleteResumes(request.getResumeIds(), principal.getName());
    }
}

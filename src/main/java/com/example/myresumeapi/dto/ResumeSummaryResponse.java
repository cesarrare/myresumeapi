package com.example.myresumeapi.dto;

import lombok.Data;

@Data
public class ResumeSummaryResponse {

    private Long resumeId;
    private Long userId;
    private String resumeName;
}

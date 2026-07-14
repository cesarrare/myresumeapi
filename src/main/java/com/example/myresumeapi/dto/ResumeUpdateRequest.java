package com.example.myresumeapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResumeUpdateRequest extends ResumeSaveRequest {

    private Long resumeId;
}

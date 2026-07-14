package com.example.myresumeapi.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResumeResponse {

    private Long userId;
    private Long resumeId;

    private String resumeName;
    private String templateName;

    private PersonalInfoResponse personalInfo;

    private List<String> coreCompetencies;

    private List<TechnicalSkillResponse> technicalSkills;

    private List<ProfessionalHistoryResponse> professionalHistory;

    private List<FeaturedProjectResponse> featuredProjects;
}
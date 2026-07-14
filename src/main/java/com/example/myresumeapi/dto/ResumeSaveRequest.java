package com.example.myresumeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeSaveRequest {

    private Long userId;
    private String resumeName;
    private String templateName;

    private PersonalInfoResponse personalInfo;
    private List<String> coreCompetencies;
    private List<TechnicalSkillResponse> technicalSkills;
    private List<ProfessionalHistoryResponse> professionalHistory;
    private List<FeaturedProjectResponse> featuredProjects;
}

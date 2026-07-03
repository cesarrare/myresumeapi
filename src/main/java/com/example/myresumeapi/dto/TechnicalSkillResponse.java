package com.example.myresumeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalSkillResponse {

    private Long id;

    private String category;

    private String skillName;

    private Integer yearsOfExperience;

    private Integer displayOrder;
}
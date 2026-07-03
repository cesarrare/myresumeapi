package com.example.myresumeapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfessionalHistoryResponse {

    private String company;
    private String role;
    private String period;
    private String location;

    private List<String> achievements;
}

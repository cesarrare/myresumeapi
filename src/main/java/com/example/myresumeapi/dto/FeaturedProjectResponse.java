package com.example.myresumeapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeaturedProjectResponse {

    private String name;
    private String description;

    private List<String> technologies;
}

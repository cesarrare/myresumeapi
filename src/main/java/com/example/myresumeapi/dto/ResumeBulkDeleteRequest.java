package com.example.myresumeapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResumeBulkDeleteRequest {

    private List<Long> resumeIds;
}

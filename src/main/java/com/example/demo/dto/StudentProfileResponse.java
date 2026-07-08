package com.example.demo.dto;

import java.util.List;

public record StudentProfileResponse(
        StudentResponse student,
        StudentStatisticsResponse statistics,
        List<ProjectResponse> projects,
        List<PublicationResponse> publications,
        List<ActivityResponse> activities
) {
}

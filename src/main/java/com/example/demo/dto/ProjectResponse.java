package com.example.demo.dto;

import com.example.demo.entity.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProjectResponse(
        String id,
        String name,
        String summary,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        List<String> technologies,
        Set<String> studentIds,
        String imagePath,
        String projectUrl,
        boolean featured,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ProjectResponse(
            String id,
            String name,
            String summary,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            ProjectStatus status,
            List<String> technologies,
            Set<String> studentIds,
            String imagePath,
            String projectUrl,
            boolean featured
    ) {
        this(
                id,
                name,
                summary,
                description,
                startDate,
                endDate,
                status,
                technologies,
                studentIds,
                imagePath,
                projectUrl,
                featured,
                null,
                null
        );
    }
}

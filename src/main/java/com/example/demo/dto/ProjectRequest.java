package com.example.demo.dto;

import com.example.demo.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record ProjectRequest(

        @NotBlank(message = "Project name cannot be empty.")
        @Size(
                max = 200,
                message = "Project name cannot exceed 200 characters."
        )
        String name,

        @NotBlank(message = "Project summary cannot be empty.")
        @Size(
                max = 500,
                message = "Project summary cannot exceed 500 characters."
        )
        String summary,

        @Size(
                max = 5000,
                message = "Project description cannot exceed 5000 characters."
        )
        String description,

        LocalDate startDate,

        LocalDate endDate,

        ProjectStatus status,

        @Size(
                max = 30,
                message = "A project cannot contain more than 30 technologies."
        )
        List<String> technologies,

        @Size(
                max = 100,
                message = "A project cannot contain more than 100 team members."
        )
        Set<String> studentIds,

        @Size(
                max = 500,
                message = "Project URL cannot exceed 500 characters."
        )
        String projectUrl,

        Boolean featured
) {
}
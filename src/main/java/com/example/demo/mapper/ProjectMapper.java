package com.example.demo.mapper;

import com.example.demo.dto.ProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static Project toEntity(
            ProjectRequest request,
            Set<String> validatedStudentIds
    ) {
        Project project = new Project();

        project.setName(request.name());
        project.setSummary(request.summary());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());

        project.setStatus(
                request.status() == null
                        ? ProjectStatus.PLANNED
                        : request.status()
        );

        project.setTechnologies(
                request.technologies() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(request.technologies())
        );

        project.setStudentIds(
                validatedStudentIds == null
                        ? new HashSet<>()
                        : new HashSet<>(validatedStudentIds)
        );

        project.setProjectUrl(request.projectUrl());

        project.setFeatured(
                request.featured() != null
                        && request.featured()
        );

        return project;
    }

    public static void updateEntity(
            Project project,
            ProjectRequest request,
            Set<String> validatedStudentIds
    ) {
        project.setName(request.name());
        project.setSummary(request.summary());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());

        if (request.status() != null) {
            project.setStatus(request.status());
        }

        project.setTechnologies(
                request.technologies() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(request.technologies())
        );

        project.setStudentIds(
                validatedStudentIds == null
                        ? new HashSet<>()
                        : new HashSet<>(validatedStudentIds)
        );

        project.setProjectUrl(request.projectUrl());

        if (request.featured() != null) {
            project.setFeatured(request.featured());
        }
    }

    public static ProjectResponse toResponse(
            Project project
    ) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getSummary(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getTechnologies() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        project.getTechnologies()
                ),
                project.getStudentIds() == null
                        ? new HashSet<>()
                        : new HashSet<>(
                        project.getStudentIds()
                ),
                project.getImagePath(),
                project.getProjectUrl(),
                project.isFeatured(),
                MongoTimestampUtil.resolveCreatedAt(
                        project.getId(),
                        project.getCreatedAt()
                ),
                project.getUpdatedAt()
        );
    }
}
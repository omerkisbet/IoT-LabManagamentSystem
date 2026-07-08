package com.example.demo.dto;

import com.example.demo.entity.MemberType;

import java.time.LocalDateTime;

public record StudentResponse(
        String id,
        String studentNumber,
        String firstName,
        String lastName,
        String email,
        String department,
        String currentTask,
        String photoPath,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberType memberType,
        String academicTitle
) {
    public StudentResponse(
            String id,
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String department,
            String currentTask,
            String photoPath,
            boolean active
    ) {
        this(
                id,
                studentNumber,
                firstName,
                lastName,
                email,
                department,
                currentTask,
                photoPath,
                active,
                null,
                null,
                MemberType.STUDENT,
                null
        );
    }

    public StudentResponse(
            String id,
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String department,
            String currentTask,
            String photoPath,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this(
                id,
                studentNumber,
                firstName,
                lastName,
                email,
                department,
                currentTask,
                photoPath,
                active,
                createdAt,
                updatedAt,
                MemberType.STUDENT,
                null
        );
    }
}

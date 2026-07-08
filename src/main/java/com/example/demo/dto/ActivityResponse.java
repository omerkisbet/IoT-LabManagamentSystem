package com.example.demo.dto;

import java.time.LocalDateTime;

public record ActivityResponse(
        String id,
        String studentId,
        String title,
        String description,
        LocalDateTime activityDate
) {
}
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ActivityRequest(

        @NotBlank(message = "Activity title cannot be empty.")
        @Size(
                max = 200,
                message = "Activity title cannot exceed 200 characters."
        )
        String title,

        @Size(
                max = 2000,
                message = "Activity description cannot exceed 2000 characters."
        )
        String description,

        LocalDateTime activityDate
) {
}
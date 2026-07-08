package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageReplyRequest(

        @NotBlank(message = "Reply subject cannot be empty.")
        @Size(
                max = 200,
                message = "Reply subject cannot exceed 200 characters."
        )
        String subject,

        @NotBlank(message = "Reply body cannot be empty.")
        @Size(
                max = 5000,
                message = "Reply body cannot exceed 5000 characters."
        )
        String body
) {
}

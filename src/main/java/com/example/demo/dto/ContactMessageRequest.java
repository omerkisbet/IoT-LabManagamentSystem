package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(

        @NotBlank(message = "Sender name cannot be empty.")
        @Size(
                max = 100,
                message = "Sender name cannot exceed 100 characters."
        )
        String senderName,

        @NotBlank(message = "Email cannot be empty.")
        @Email(message = "Please enter a valid email address.")
        @Size(
                max = 150,
                message = "Email cannot exceed 150 characters."
        )
        String email,

        @NotBlank(message = "Subject cannot be empty.")
        @Size(
                max = 200,
                message = "Subject cannot exceed 200 characters."
        )
        String subject,

        @NotBlank(message = "Message cannot be empty.")
        @Size(
                max = 5000,
                message = "Message cannot exceed 5000 characters."
        )
        String message
) {
}
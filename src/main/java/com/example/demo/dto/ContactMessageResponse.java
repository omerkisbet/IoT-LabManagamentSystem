package com.example.demo.dto;

import com.example.demo.entity.ContactStatus;

import java.time.LocalDateTime;

public record ContactMessageResponse(
        String id,
        String senderName,
        String email,
        String subject,
        String message,
        LocalDateTime sentAt,
        ContactStatus status
) {
}
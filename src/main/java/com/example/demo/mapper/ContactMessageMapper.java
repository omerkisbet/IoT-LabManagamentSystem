package com.example.demo.mapper;

import com.example.demo.dto.ContactMessageRequest;
import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.entity.ContactMessage;

public final class ContactMessageMapper {

    private ContactMessageMapper() {
    }

    public static ContactMessage toEntity(
            ContactMessageRequest request
    ) {
        ContactMessage contactMessage =
                new ContactMessage();

        contactMessage.setSenderName(
                request.senderName().trim()
        );

        contactMessage.setEmail(
                request.email().trim().toLowerCase()
        );

        contactMessage.setSubject(
                request.subject().trim()
        );

        contactMessage.setMessage(
                request.message().trim()
        );

        return contactMessage;
    }

    public static ContactMessageResponse toResponse(
            ContactMessage contactMessage
    ) {
        return new ContactMessageResponse(
                contactMessage.getId(),
                contactMessage.getSenderName(),
                contactMessage.getEmail(),
                contactMessage.getSubject(),
                contactMessage.getMessage(),
                contactMessage.getSentAt(),
                contactMessage.getStatus()
        );
    }
}
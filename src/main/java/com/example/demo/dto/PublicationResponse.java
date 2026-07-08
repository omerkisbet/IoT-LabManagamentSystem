package com.example.demo.dto;

import com.example.demo.entity.PublicationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PublicationResponse(
        String id,
        String title,
        List<String> authors,
        String venue,
        int publicationYear,
        PublicationType type,
        String doi,
        String publicationUrl,
        String abstractText,
        Set<String> studentIds,
        boolean featured,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public PublicationResponse(
            String id,
            String title,
            List<String> authors,
            String venue,
            int publicationYear,
            PublicationType type,
            String doi,
            String publicationUrl,
            String abstractText,
            Set<String> studentIds,
            boolean featured
    ) {
        this(
                id,
                title,
                authors,
                venue,
                publicationYear,
                type,
                doi,
                publicationUrl,
                abstractText,
                studentIds,
                featured,
                null,
                null
        );
    }
}

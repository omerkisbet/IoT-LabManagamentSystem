package com.example.demo.dto;

import com.example.demo.entity.PublicationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record PublicationRequest(

        @NotBlank(message = "Publication title cannot be empty.")
        @Size(
                max = 300,
                message = "Publication title cannot exceed 300 characters."
        )
        String title,

        @NotEmpty(message = "At least one author must be entered.")
        @Size(
                max = 50,
                message = "A publication cannot contain more than 50 authors."
        )
        List<
                @NotBlank(message = "Author name cannot be empty.")
                @Size(
                        max = 200,
                        message = "Author name cannot exceed 200 characters."
                )
                        String
                > authors,

        @NotBlank(message = "Journal or conference name cannot be empty.")
        @Size(
                max = 300,
                message = "Venue cannot exceed 300 characters."
        )
        String venue,

        @Min(
                value = 1900,
                message = "Publication year cannot be earlier than 1900."
        )
        int publicationYear,

        PublicationType type,

        @Size(
                max = 200,
                message = "DOI cannot exceed 200 characters."
        )
        String doi,

        @Size(
                max = 500,
                message = "Publication URL cannot exceed 500 characters."
        )
        String publicationUrl,

        @Size(
                max = 10000,
                message = "Abstract cannot exceed 10000 characters."
        )
        String abstractText,

        @Size(
                max = 100,
                message = "A publication cannot contain more than 100 registered team members."
        )
        Set<
                @NotBlank(message = "Team member ID cannot be empty.")
                        String
                > studentIds,

        Boolean featured
) {
}
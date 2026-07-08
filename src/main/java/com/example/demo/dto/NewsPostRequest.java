package com.example.demo.dto;

import com.example.demo.entity.NewsCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record NewsPostRequest(

        @NotBlank(message = "News title cannot be empty.")
        @Size(
                max = 200,
                message = "News title cannot exceed 200 characters."
        )
        String title,

        @NotBlank(message = "News summary cannot be empty.")
        @Size(
                max = 500,
                message = "News summary cannot exceed 500 characters."
        )
        String summary,

        @NotBlank(message = "News content cannot be empty.")
        @Size(
                max = 20000,
                message = "News content cannot exceed 20000 characters."
        )
        String content,

        NewsCategory category,

        LocalDateTime publishedAt,

        Boolean active,

        Boolean featured,

        @Size(
                max = 100,
                message = "Related entity ID cannot exceed 100 characters."
        )
        String relatedEntityId
) {
}
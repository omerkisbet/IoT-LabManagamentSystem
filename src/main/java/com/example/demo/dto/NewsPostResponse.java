package com.example.demo.dto;

import com.example.demo.entity.NewsCategory;

import java.time.LocalDateTime;

public record NewsPostResponse(
        String id,
        String title,
        String summary,
        String content,
        NewsCategory category,
        String imagePath,
        LocalDateTime publishedAt,
        boolean active,
        boolean featured,
        String relatedEntityId
) {
}
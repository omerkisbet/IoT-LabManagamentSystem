package com.example.demo.mapper;

import com.example.demo.dto.NewsPostRequest;
import com.example.demo.dto.NewsPostResponse;
import com.example.demo.entity.NewsCategory;
import com.example.demo.entity.NewsPost;

public final class NewsPostMapper {

    private NewsPostMapper() {
    }

    public static NewsPost toEntity(
            NewsPostRequest request
    ) {
        NewsPost newsPost = new NewsPost();

        newsPost.setTitle(request.title());
        newsPost.setSummary(request.summary());
        newsPost.setContent(request.content());

        newsPost.setCategory(
                request.category() == null
                        ? NewsCategory.NEWS
                        : request.category()
        );

        newsPost.setPublishedAt(
                request.publishedAt()
        );

        newsPost.setActive(
                request.active() == null
                        || request.active()
        );

        newsPost.setFeatured(
                request.featured() != null
                        && request.featured()
        );

        newsPost.setRelatedEntityId(
                request.relatedEntityId()
        );

        return newsPost;
    }

    public static void updateEntity(
            NewsPost newsPost,
            NewsPostRequest request
    ) {
        newsPost.setTitle(request.title());
        newsPost.setSummary(request.summary());
        newsPost.setContent(request.content());

        if (request.category() != null) {
            newsPost.setCategory(request.category());
        }

        if (request.publishedAt() != null) {
            newsPost.setPublishedAt(
                    request.publishedAt()
            );
        }

        if (request.active() != null) {
            newsPost.setActive(request.active());
        }

        if (request.featured() != null) {
            newsPost.setFeatured(
                    request.featured()
            );
        }

        newsPost.setRelatedEntityId(
                request.relatedEntityId()
        );
    }

    public static NewsPostResponse toResponse(
            NewsPost newsPost
    ) {
        return new NewsPostResponse(
                newsPost.getId(),
                newsPost.getTitle(),
                newsPost.getSummary(),
                newsPost.getContent(),
                newsPost.getCategory(),
                newsPost.getImagePath(),
                newsPost.getPublishedAt(),
                newsPost.isActive(),
                newsPost.isFeatured(),
                newsPost.getRelatedEntityId()
        );
    }
}

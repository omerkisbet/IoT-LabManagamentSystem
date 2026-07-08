package com.example.demo.service;

import com.example.demo.dto.NewsPostRequest;
import com.example.demo.dto.NewsPostResponse;
import com.example.demo.entity.NewsCategory;
import com.example.demo.entity.NewsPost;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.NewsPostMapper;
import com.example.demo.repository.NewsPostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsPostService {

    private final NewsPostRepository newsPostRepository;
    private final FileStorageService fileStorageService;

    public NewsPostService(
            NewsPostRepository newsPostRepository,
            FileStorageService fileStorageService
    ) {
        this.newsPostRepository = newsPostRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<NewsPostResponse> getAllNewsPosts() {
        return newsPostRepository
                .findAllByOrderByPublishedAtDesc()
                .stream()
                .map(NewsPostMapper::toResponse)
                .toList();
    }

    public List<NewsPostResponse> getActiveNewsPosts() {
        return newsPostRepository
                .findByActiveTrueOrderByPublishedAtDesc()
                .stream()
                .map(NewsPostMapper::toResponse)
                .toList();
    }

    public NewsPostResponse getNewsPostById(
            String id
    ) {
        return NewsPostMapper.toResponse(
                findNewsPostEntityById(id)
        );
    }

    public NewsPostResponse createNewsPost(
            NewsPostRequest request
    ) {
        NewsPost newsPost =
                NewsPostMapper.toEntity(request);

        if (newsPost.getPublishedAt() == null) {
            newsPost.setPublishedAt(
                    LocalDateTime.now()
            );
        }

        NewsPost savedNewsPost =
                newsPostRepository.save(newsPost);

        return NewsPostMapper.toResponse(
                savedNewsPost
        );
    }

    public NewsPostResponse updateNewsPost(
            String id,
            NewsPostRequest request
    ) {
        NewsPost existingNewsPost =
                findNewsPostEntityById(id);

        NewsPostMapper.updateEntity(
                existingNewsPost,
                request
        );

        NewsPost savedNewsPost =
                newsPostRepository.save(
                        existingNewsPost
                );

        return NewsPostMapper.toResponse(
                savedNewsPost
        );
    }

    public void deleteNewsPost(String id) {
        NewsPost newsPost =
                findNewsPostEntityById(id);

        String imagePath =
                newsPost.getImagePath();

        newsPostRepository.delete(newsPost);

        fileStorageService.deleteImageByUrl(
                imagePath
        );
    }

    public List<NewsPostResponse> getNewsPostsByCategory(
            NewsCategory category
    ) {
        return newsPostRepository
                .findByCategoryAndActiveTrueOrderByPublishedAtDesc(
                        category
                )
                .stream()
                .map(NewsPostMapper::toResponse)
                .toList();
    }

    public List<NewsPostResponse> getFeaturedNewsPosts() {
        return newsPostRepository
                .findByFeaturedTrueAndActiveTrueOrderByPublishedAtDesc()
                .stream()
                .map(NewsPostMapper::toResponse)
                .toList();
    }

    public List<NewsPostResponse> searchNewsPosts(
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        String normalizedKeyword =
                keyword.trim();

        return newsPostRepository
                .findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrderByPublishedAtDesc(
                        normalizedKeyword,
                        normalizedKeyword
                )
                .stream()
                .map(NewsPostMapper::toResponse)
                .toList();
    }

    public NewsPostResponse changeNewsPostVisibility(
            String id,
            boolean active
    ) {
        NewsPost newsPost =
                findNewsPostEntityById(id);

        newsPost.setActive(active);

        NewsPost savedNewsPost =
                newsPostRepository.save(newsPost);

        return NewsPostMapper.toResponse(
                savedNewsPost
        );
    }

    public NewsPostResponse changeFeaturedStatus(
            String id,
            boolean featured
    ) {
        NewsPost newsPost =
                findNewsPostEntityById(id);

        newsPost.setFeatured(featured);

        NewsPost savedNewsPost =
                newsPostRepository.save(newsPost);

        return NewsPostMapper.toResponse(
                savedNewsPost
        );
    }

    public NewsPostResponse updateImagePath(
            String newsId,
            String imagePath
    ) {
        NewsPost newsPost =
                findNewsPostEntityById(newsId);

        newsPost.setImagePath(imagePath);

        NewsPost savedNewsPost =
                newsPostRepository.save(newsPost);

        return NewsPostMapper.toResponse(
                savedNewsPost
        );
    }

    private NewsPost findNewsPostEntityById(
            String id
    ) {
        return newsPostRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "News post not found. ID: "
                                        + id
                        )
                );
    }
}
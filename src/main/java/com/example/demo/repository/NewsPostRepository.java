package com.example.demo.repository;

import com.example.demo.entity.NewsCategory;
import com.example.demo.entity.NewsPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsPostRepository
        extends MongoRepository<NewsPost, String> {

    List<NewsPost> findAllByOrderByPublishedAtDesc();

    List<NewsPost> findByActiveTrueOrderByPublishedAtDesc();

    List<NewsPost>
    findByCategoryAndActiveTrueOrderByPublishedAtDesc(
            NewsCategory category
    );

    List<NewsPost>
    findByFeaturedTrueAndActiveTrueOrderByPublishedAtDesc();

    List<NewsPost>
    findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrderByPublishedAtDesc(
            String title,
            String summary
    );
}
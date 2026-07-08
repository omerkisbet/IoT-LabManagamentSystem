package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "news_posts")
public class NewsPost {

    @Id
    private String id;

    @NotBlank(message = "News title cannot be empty.")
    @Size(max = 200, message = "News title cannot exceed 200 characters.")
    private String title;

    @NotBlank(message = "News summary cannot be empty.")
    @Size(max = 500, message = "News summary cannot exceed 500 characters.")
    private String summary;

    @NotBlank(message = "News content cannot be empty.")
    private String content;

    private NewsCategory category = NewsCategory.NEWS;

    private String imagePath;

    private LocalDateTime publishedAt;

    private boolean active = true;

    private boolean featured;

    /*
     * Haber bir ekip üyesi, proje veya yayın ile ilişkiliyse
     * ilgili MongoDB ID burada tutulabilir.
     */
    private String relatedEntityId;

    public NewsPost() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
}
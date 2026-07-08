package com.example.demo.Controller;

import com.example.demo.dto.NewsPostRequest;
import com.example.demo.dto.NewsPostResponse;
import com.example.demo.entity.NewsCategory;
import com.example.demo.service.NewsPostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsPostController {

    private final NewsPostService newsPostService;

    public NewsPostController(
            NewsPostService newsPostService
    ) {
        this.newsPostService = newsPostService;
    }

    @GetMapping
    public List<NewsPostResponse> getAllNewsPosts() {
        return newsPostService.getAllNewsPosts();
    }

    @GetMapping("/active")
    public List<NewsPostResponse> getActiveNewsPosts() {
        return newsPostService.getActiveNewsPosts();
    }

    @GetMapping("/featured")
    public List<NewsPostResponse> getFeaturedNewsPosts() {
        return newsPostService
                .getFeaturedNewsPosts();
    }

    @GetMapping("/category/{category}")
    public List<NewsPostResponse> getNewsPostsByCategory(
            @PathVariable NewsCategory category
    ) {
        return newsPostService
                .getNewsPostsByCategory(category);
    }

    @GetMapping("/search")
    public List<NewsPostResponse> searchNewsPosts(
            @RequestParam String keyword
    ) {
        return newsPostService
                .searchNewsPosts(keyword);
    }

    @GetMapping("/{id}")
    public NewsPostResponse getNewsPostById(
            @PathVariable String id
    ) {
        return newsPostService
                .getNewsPostById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsPostResponse createNewsPost(
            @Valid
            @RequestBody
            NewsPostRequest request
    ) {
        return newsPostService
                .createNewsPost(request);
    }

    @PutMapping("/{id}")
    public NewsPostResponse updateNewsPost(
            @PathVariable String id,
            @Valid
            @RequestBody
            NewsPostRequest request
    ) {
        return newsPostService
                .updateNewsPost(id, request);
    }

    @PatchMapping("/{id}/visibility")
    public NewsPostResponse changeVisibility(
            @PathVariable String id,
            @RequestParam boolean active
    ) {
        return newsPostService
                .changeNewsPostVisibility(
                        id,
                        active
                );
    }

    @PatchMapping("/{id}/featured")
    public NewsPostResponse changeFeaturedStatus(
            @PathVariable String id,
            @RequestParam boolean featured
    ) {
        return newsPostService
                .changeFeaturedStatus(
                        id,
                        featured
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNewsPost(
            @PathVariable String id
    ) {
        newsPostService.deleteNewsPost(id);
    }
}
package com.example.demo.Controller;

import com.example.demo.dto.PublicationRequest;
import com.example.demo.dto.PublicationResponse;
import com.example.demo.entity.PublicationType;
import com.example.demo.service.PublicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private final PublicationService publicationService;

    public PublicationController(
            PublicationService publicationService
    ) {
        this.publicationService = publicationService;
    }

    @GetMapping
    public List<PublicationResponse> getAllPublications() {
        return publicationService
                .getAllPublications();
    }

    @GetMapping("/featured")
    public List<PublicationResponse>
    getFeaturedPublications() {
        return publicationService
                .getFeaturedPublications();
    }

    @GetMapping("/type/{type}")
    public List<PublicationResponse>
    getPublicationsByType(
            @PathVariable PublicationType type
    ) {
        return publicationService
                .getPublicationsByType(type);
    }

    @GetMapping("/year/{year}")
    public List<PublicationResponse>
    getPublicationsByYear(
            @PathVariable int year
    ) {
        return publicationService
                .getPublicationsByYear(year);
    }

    @GetMapping("/search")
    public List<PublicationResponse> searchPublications(
            @RequestParam String keyword
    ) {
        return publicationService
                .searchByTitle(keyword);
    }

    @GetMapping("/student/{studentId}")
    public List<PublicationResponse>
    getPublicationsByStudent(
            @PathVariable String studentId
    ) {
        return publicationService
                .getPublicationsByStudent(studentId);
    }

    @GetMapping("/{id}")
    public PublicationResponse getPublicationById(
            @PathVariable String id
    ) {
        return publicationService
                .getPublicationById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicationResponse createPublication(
            @Valid
            @RequestBody
            PublicationRequest request
    ) {
        return publicationService
                .createPublication(request);
    }

    @PutMapping("/{id}")
    public PublicationResponse updatePublication(
            @PathVariable String id,
            @Valid
            @RequestBody
            PublicationRequest request
    ) {
        return publicationService
                .updatePublication(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublication(
            @PathVariable String id
    ) {
        publicationService.deletePublication(id);
    }
}
package com.example.demo.repository;

import com.example.demo.entity.Publication;
import com.example.demo.entity.PublicationType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PublicationRepository
        extends MongoRepository<Publication, String> {

    List<Publication> findAllByOrderByPublicationYearDesc();

    List<Publication> findByTypeOrderByPublicationYearDesc(
            PublicationType type
    );

    List<Publication> findByPublicationYearOrderByTitleAsc(
            int publicationYear
    );

    List<Publication> findByFeaturedTrueOrderByPublicationYearDesc();

    List<Publication> findByTitleContainingIgnoreCase(
            String title
    );

    List<Publication> findByStudentIdsContaining(
            String studentId
    );

    boolean existsByDoi(String doi);
}
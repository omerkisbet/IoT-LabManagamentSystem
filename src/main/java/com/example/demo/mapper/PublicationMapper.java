package com.example.demo.mapper;

import com.example.demo.dto.PublicationRequest;
import com.example.demo.dto.PublicationResponse;
import com.example.demo.entity.Publication;
import com.example.demo.entity.PublicationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class PublicationMapper {

    private PublicationMapper() {
    }

    public static Publication toEntity(
            PublicationRequest request,
            Set<String> validatedStudentIds
    ) {
        Publication publication = new Publication();

        publication.setTitle(request.title());

        publication.setAuthors(
                request.authors() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(request.authors())
        );

        publication.setVenue(request.venue());

        publication.setPublicationYear(
                request.publicationYear()
        );

        publication.setType(
                request.type() == null
                        ? PublicationType.JOURNAL_ARTICLE
                        : request.type()
        );

        publication.setDoi(request.doi());

        publication.setPublicationUrl(
                request.publicationUrl()
        );

        publication.setAbstractText(
                request.abstractText()
        );

        publication.setStudentIds(
                validatedStudentIds == null
                        ? new HashSet<>()
                        : new HashSet<>(validatedStudentIds)
        );

        publication.setFeatured(
                request.featured() != null
                        && request.featured()
        );

        return publication;
    }

    public static void updateEntity(
            Publication publication,
            PublicationRequest request,
            Set<String> validatedStudentIds
    ) {
        publication.setTitle(request.title());

        publication.setAuthors(
                request.authors() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(request.authors())
        );

        publication.setVenue(request.venue());

        publication.setPublicationYear(
                request.publicationYear()
        );

        publication.setType(
                request.type() == null
                        ? PublicationType.JOURNAL_ARTICLE
                        : request.type()
        );

        publication.setDoi(request.doi());

        publication.setPublicationUrl(
                request.publicationUrl()
        );

        publication.setAbstractText(
                request.abstractText()
        );

        publication.setStudentIds(
                validatedStudentIds == null
                        ? new HashSet<>()
                        : new HashSet<>(validatedStudentIds)
        );

        if (request.featured() != null) {
            publication.setFeatured(
                    request.featured()
            );
        }
    }

    public static PublicationResponse toResponse(
            Publication publication
    ) {
        return new PublicationResponse(
                publication.getId(),
                publication.getTitle(),

                publication.getAuthors() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        publication.getAuthors()
                ),

                publication.getVenue(),
                publication.getPublicationYear(),
                publication.getType(),
                publication.getDoi(),
                publication.getPublicationUrl(),
                publication.getAbstractText(),

                publication.getStudentIds() == null
                        ? new HashSet<>()
                        : new HashSet<>(
                        publication.getStudentIds()
                ),

                publication.isFeatured(),
                MongoTimestampUtil.resolveCreatedAt(
                        publication.getId(),
                        publication.getCreatedAt()
                ),
                publication.getUpdatedAt()
        );
    }
}
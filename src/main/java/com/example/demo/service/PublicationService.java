package com.example.demo.service;

import com.example.demo.dto.PublicationRequest;
import com.example.demo.dto.PublicationResponse;
import com.example.demo.entity.Publication;
import com.example.demo.entity.PublicationType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PublicationMapper;
import com.example.demo.repository.PublicationRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final StudentRepository studentRepository;

    public PublicationService(
            PublicationRepository publicationRepository,
            StudentRepository studentRepository
    ) {
        this.publicationRepository = publicationRepository;
        this.studentRepository = studentRepository;
    }

    public List<PublicationResponse> getAllPublications() {
        return publicationRepository
                .findAll()
                .stream()
                .map(PublicationMapper::toResponse)
                .sorted(Comparator.comparing(
                        PublicationResponse::createdAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    public PublicationResponse getPublicationById(
            String id
    ) {
        return PublicationMapper.toResponse(
                findPublicationEntityById(id)
        );
    }

    public PublicationResponse createPublication(
            PublicationRequest request
    ) {
        validatePublicationYear(
                request.publicationYear()
        );

        String normalizedDoi =
                normalizeOptionalText(request.doi());

        validateDoiForCreate(normalizedDoi);

        Set<String> validatedStudentIds =
                validateStudentIds(request.studentIds());

        Publication publication =
                PublicationMapper.toEntity(
                        request,
                        validatedStudentIds
                );

        publication.setDoi(normalizedDoi);

        Publication savedPublication =
                publicationRepository.save(publication);

        return PublicationMapper.toResponse(
                savedPublication
        );
    }

    public PublicationResponse updatePublication(
            String id,
            PublicationRequest request
    ) {
        Publication existingPublication =
                findPublicationEntityById(id);

        validatePublicationYear(
                request.publicationYear()
        );

        String normalizedDoi =
                normalizeOptionalText(request.doi());

        validateDoiForUpdate(
                existingPublication,
                normalizedDoi
        );

        Set<String> validatedStudentIds =
                validateStudentIds(request.studentIds());

        PublicationMapper.updateEntity(
                existingPublication,
                request,
                validatedStudentIds
        );

        existingPublication.setDoi(normalizedDoi);

        Publication savedPublication =
                publicationRepository.save(
                        existingPublication
                );

        return PublicationMapper.toResponse(
                savedPublication
        );
    }

    public void deletePublication(String id) {
        Publication publication =
                findPublicationEntityById(id);

        publicationRepository.delete(publication);
    }

    public List<PublicationResponse> getPublicationsByType(
            PublicationType type
    ) {
        return publicationRepository
                .findByTypeOrderByPublicationYearDesc(type)
                .stream()
                .map(PublicationMapper::toResponse)
                .toList();
    }

    public List<PublicationResponse> getPublicationsByYear(
            int year
    ) {
        validatePublicationYear(year);

        return publicationRepository
                .findByPublicationYearOrderByTitleAsc(year)
                .stream()
                .map(PublicationMapper::toResponse)
                .toList();
    }

    public List<PublicationResponse> getFeaturedPublications() {
        return publicationRepository
                .findByFeaturedTrueOrderByPublicationYearDesc()
                .stream()
                .map(PublicationMapper::toResponse)
                .toList();
    }

    public List<PublicationResponse> searchByTitle(
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        return publicationRepository
                .findByTitleContainingIgnoreCase(
                        keyword.trim()
                )
                .stream()
                .map(PublicationMapper::toResponse)
                .toList();
    }

    public List<PublicationResponse> getPublicationsByStudent(
            String studentId
    ) {
        validateStudentExists(studentId);

        return publicationRepository
                .findByStudentIdsContaining(studentId)
                .stream()
                .map(PublicationMapper::toResponse)
                .toList();
    }

    private Publication findPublicationEntityById(
            String id
    ) {
        return publicationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Publication not found. ID: "
                                        + id
                        )
                );
    }

    private Set<String> validateStudentIds(
            Set<String> studentIds
    ) {
        if (studentIds == null) {
            return new HashSet<>();
        }

        Set<String> validatedIds =
                new HashSet<>(studentIds);

        for (String studentId : validatedIds) {
            validateStudentExists(studentId);
        }

        return validatedIds;
    }

    private void validateStudentExists(
            String studentId
    ) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException(
                    "Student not found. ID: "
                            + studentId
            );
        }
    }

    private void validatePublicationYear(int year) {
        int maximumYear =
                Year.now().getValue() + 1;

        if (year < 1900 || year > maximumYear) {
            throw new IllegalArgumentException(
                    "Publication year must be between 1900 and "
                            + maximumYear
                            + "."
            );
        }
    }

    private void validateDoiForCreate(
            String doi
    ) {
        if (doi != null
                && publicationRepository.existsByDoi(doi)) {

            throw new IllegalArgumentException(
                    "This DOI is already registered."
            );
        }
    }

    private void validateDoiForUpdate(
            Publication existingPublication,
            String newDoi
    ) {
        String existingDoi =
                normalizeOptionalText(
                        existingPublication.getDoi()
                );

        boolean doiChanged =
                !Objects.equals(existingDoi, newDoi);

        if (doiChanged
                && newDoi != null
                && publicationRepository.existsByDoi(newDoi)) {

            throw new IllegalArgumentException(
                    "This DOI is already registered."
            );
        }
    }

    private String normalizeOptionalText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
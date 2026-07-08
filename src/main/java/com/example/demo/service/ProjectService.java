package com.example.demo.service;

import com.example.demo.dto.ProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;

    public ProjectService(
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            FileStorageService fileStorageService
    ) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository
                .findAll()
                .stream()
                .map(ProjectMapper::toResponse)
                .sorted(Comparator.comparing(
                        ProjectResponse::createdAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    public ProjectResponse getProjectById(String id) {
        return ProjectMapper.toResponse(
                findProjectEntityById(id)
        );
    }

    public ProjectResponse createProject(
            ProjectRequest request
    ) {
        validateProjectDates(request);

        Set<String> validatedStudentIds =
                validateStudentIds(request.studentIds());

        Project project =
                ProjectMapper.toEntity(
                        request,
                        validatedStudentIds
                );

        Project savedProject =
                projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    public ProjectResponse updateProject(
            String id,
            ProjectRequest request
    ) {
        Project existingProject =
                findProjectEntityById(id);

        validateProjectDates(request);

        Set<String> validatedStudentIds =
                validateStudentIds(request.studentIds());

        ProjectMapper.updateEntity(
                existingProject,
                request,
                validatedStudentIds
        );

        Project savedProject =
                projectRepository.save(existingProject);

        return ProjectMapper.toResponse(savedProject);
    }

    public void deleteProject(String id) {
        Project project =
                findProjectEntityById(id);

        String imagePath =
                project.getImagePath();

        projectRepository.delete(project);

        fileStorageService.deleteImageByUrl(imagePath);
    }

    public List<ProjectResponse> getProjectsByStatus(
            ProjectStatus status
    ) {
        return projectRepository
                .findByStatusOrderByStartDateDesc(status)
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    public List<ProjectResponse> getFeaturedProjects() {
        return projectRepository
                .findByFeaturedTrueOrderByStartDateDesc()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    public List<ProjectResponse> getProjectsByStudentId(
            String studentId
    ) {
        validateStudentExists(studentId);

        return projectRepository
                .findByStudentId(studentId)
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    public ProjectResponse addStudentToProject(
            String projectId,
            String studentId
    ) {
        Project project =
                findProjectEntityById(projectId);

        validateStudentExists(studentId);

        if (project.getStudentIds() == null) {
            project.setStudentIds(new HashSet<>());
        }

        project.getStudentIds().add(studentId);

        Project savedProject =
                projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    public ProjectResponse removeStudentFromProject(
            String projectId,
            String studentId
    ) {
        Project project =
                findProjectEntityById(projectId);

        if (project.getStudentIds() != null) {
            project.getStudentIds().remove(studentId);
        }

        Project savedProject =
                projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    public ProjectResponse updateImagePath(
            String projectId,
            String imagePath
    ) {
        Project project =
                findProjectEntityById(projectId);

        project.setImagePath(imagePath);

        Project savedProject =
                projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    private Project findProjectEntityById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found. ID: " + id
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
                    "Student not found. ID: " + studentId
            );
        }
    }

    private void validateProjectDates(
            ProjectRequest request
    ) {
        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate()
                .isBefore(request.startDate())) {

            throw new IllegalArgumentException(
                    "Project end date cannot be before start date."
            );
        }
    }
}
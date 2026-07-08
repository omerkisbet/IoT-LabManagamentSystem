package com.example.demo.service;

import com.example.demo.dto.ActivityResponse;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.dto.PublicationResponse;
import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.dto.StudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.dto.StudentStatisticsResponse;
import com.example.demo.entity.Student;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ActivityMapper;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.mapper.PublicationMapper;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Project;
import com.example.demo.entity.Publication;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.PublicationRepository;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Objects;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final PublicationRepository publicationRepository;

    public StudentService(
            StudentRepository studentRepository,
            FileStorageService fileStorageService, ActivityRepository activityRepository, ProjectRepository projectRepository, PublicationRepository publicationRepository
    ) {
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.publicationRepository = publicationRepository;
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toResponse)
                .sorted(Comparator.comparing(
                        StudentResponse::createdAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    public StudentResponse getStudentById(String id) {
        return StudentMapper.toResponse(
                findStudentEntityById(id)
        );
    }

    public StudentProfileResponse getStudentProfile(
            String id
    ) {
        Student student = findStudentEntityById(id);

        List<ProjectResponse> projects =
                projectRepository.findByStudentId(id)
                        .stream()
                        .map(ProjectMapper::toResponse)
                        .sorted(Comparator.comparing(
                                ProjectResponse::createdAt,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        ))
                        .toList();

        List<PublicationResponse> publications =
                publicationRepository
                        .findByStudentIdsContaining(id)
                        .stream()
                        .map(PublicationMapper::toResponse)
                        .sorted(Comparator.comparing(
                                PublicationResponse::createdAt,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        ))
                        .toList();

        List<ActivityResponse> activities =
                activityRepository
                        .findByStudentIdOrderByActivityDateDesc(id)
                        .stream()
                        .map(ActivityMapper::toResponse)
                        .toList();

        StudentStatisticsResponse statistics =
                new StudentStatisticsResponse(
                        projects.size(),
                        publications.size(),
                        activities.size(),
                        (long) projects.size()
                                + publications.size()
                                + activities.size()
                );

        return new StudentProfileResponse(
                StudentMapper.toResponse(student),
                statistics,
                projects,
                publications,
                activities
        );
    }

    public StudentResponse addStudent(
            StudentRequest request
    ) {
        if (studentRepository.existsByStudentNumber(
                request.studentNumber()
        )) {
            throw new IllegalArgumentException(
                    "Member number is already registered."
            );
        }

        Student student =
                StudentMapper.toEntity(request);

        Student savedStudent =
                studentRepository.save(student);

        return StudentMapper.toResponse(savedStudent);
    }

    public StudentResponse updateStudent(
            String id,
            StudentRequest request
    ) {
        Student existingStudent =
                findStudentEntityById(id);

        boolean studentNumberChanged =
                !Objects.equals(
                        existingStudent.getStudentNumber(),
                        request.studentNumber()
                );

        if (studentNumberChanged
                && studentRepository.existsByStudentNumber(
                request.studentNumber()
        )) {

            throw new IllegalArgumentException(
                    "Member number is already registered."
            );
        }

        StudentMapper.updateEntity(
                existingStudent,
                request
        );

        Student savedStudent =
                studentRepository.save(existingStudent);

        return StudentMapper.toResponse(savedStudent);
    }

    public void deleteStudent(String id) {

        Student student =
                findStudentEntityById(id);

        String photoPath =
                student.getPhotoPath();

        deleteStudentActivities(id);

        removeStudentFromProjects(id);

        removeStudentFromPublications(id);

        studentRepository.delete(student);

        fileStorageService.deleteImageByUrl(
                photoPath
        );
    }

    public StudentResponse getStudentByStudentNumber(
            String studentNumber
    ) {
        Student student = studentRepository
                .findByStudentNumber(studentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found: "
                                        + studentNumber
                        )
                );

        return StudentMapper.toResponse(student);
    }

    public List<StudentResponse> getStudentsByDepartment(
            String department
    ) {
        return studentRepository
                .findByDepartmentIgnoreCase(department)
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    public List<StudentResponse> searchStudents(
            String keyword
    ) {
        return studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        keyword,
                        keyword
                )
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    public StudentResponse updatePhotoPath(
            String studentId,
            String photoPath
    ) {
        Student student =
                findStudentEntityById(studentId);

        student.setPhotoPath(photoPath);

        Student savedStudent =
                studentRepository.save(student);

        return StudentMapper.toResponse(savedStudent);
    }
    private void deleteStudentActivities(
            String studentId
    ) {
        activityRepository.deleteByStudentId(
                studentId
        );
    }
    private void removeStudentFromProjects(
            String studentId
    ) {
        List<Project> projects =
                projectRepository.findByStudentId(
                        studentId
                );

        List<Project> changedProjects =
                new ArrayList<>();

        for (Project project : projects) {

            if (project.getStudentIds() != null
                    && project.getStudentIds()
                    .remove(studentId)) {

                changedProjects.add(project);
            }
        }

        if (!changedProjects.isEmpty()) {
            projectRepository.saveAll(
                    changedProjects
            );
        }
    }
    private void removeStudentFromPublications(
            String studentId
    ) {
        List<Publication> publications =
                publicationRepository
                        .findByStudentIdsContaining(
                                studentId
                        );

        List<Publication> changedPublications =
                new ArrayList<>();

        for (Publication publication : publications) {

            if (publication.getStudentIds() != null
                    && publication.getStudentIds()
                    .remove(studentId)) {

                changedPublications.add(
                        publication
                );
            }
        }

        if (!changedPublications.isEmpty()) {
            publicationRepository.saveAll(
                    changedPublications
            );
        }
    }

    private Student findStudentEntityById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found. ID: " + id
                        )
                );
    }
}
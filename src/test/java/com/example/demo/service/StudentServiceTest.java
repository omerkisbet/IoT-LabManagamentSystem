package com.example.demo.service;

import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.dto.StudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.entity.Activity;
import com.example.demo.entity.MemberType;
import com.example.demo.entity.Project;
import com.example.demo.entity.Publication;
import com.example.demo.entity.Student;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.PublicationRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudentShouldSaveAndReturnResponse() {

        StudentRequest request =
                new StudentRequest(
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "REST API development",
                        true
                );

        when(
                studentRepository.existsByStudentNumber(
                        "20230001"
                )
        ).thenReturn(false);

        /*
         * Repository save edildiğinde MongoDB tarafından
         * oluşturulmuş gibi öğrenciye ID ekliyoruz.
         */
        when(
                studentRepository.save(
                        any(Student.class)
                )
        ).thenAnswer(invocation -> {

            Student student =
                    invocation.getArgument(0);

            student.setId("student-id-1");

            return student;
        });

        StudentResponse response =
                studentService.addStudent(request);

        assertEquals(
                "student-id-1",
                response.id()
        );

        assertEquals(
                "20230001",
                response.studentNumber()
        );

        assertEquals(
                "Ali",
                response.firstName()
        );

        assertEquals(
                "Yilmaz",
                response.lastName()
        );

        assertEquals(
                "ali@example.com",
                response.email()
        );

        assertEquals(
                "Computer Engineering",
                response.department()
        );

        assertEquals(
                "REST API development",
                response.currentTask()
        );

        assertEquals(
                true,
                response.active()
        );

        assertEquals(
                MemberType.STUDENT,
                response.memberType()
        );

        verify(
                studentRepository
        ).existsByStudentNumber(
                "20230001"
        );

        verify(
                studentRepository
        ).save(
                any(Student.class)
        );
    }

    @Test
    void addAcademicianShouldSaveMemberTypeAndAcademicTitle() {

        StudentRequest request =
                new StudentRequest(
                        "ACA-1001",
                        "Aylin",
                        "Demir",
                        "aylin@example.com",
                        "Computer Engineering",
                        "Network security research",
                        true,
                        MemberType.ACADEMICIAN,
                        "Prof. Dr."
                );

        when(
                studentRepository.existsByStudentNumber(
                        "ACA-1001"
                )
        ).thenReturn(false);

        when(
                studentRepository.save(
                        any(Student.class)
                )
        ).thenAnswer(invocation -> {
            Student student =
                    invocation.getArgument(0);

            student.setId("academician-id-1");

            return student;
        });

        StudentResponse response =
                studentService.addStudent(request);

        assertEquals(
                "academician-id-1",
                response.id()
        );

        assertEquals(
                MemberType.ACADEMICIAN,
                response.memberType()
        );

        assertEquals(
                "Prof. Dr.",
                response.academicTitle()
        );

        verify(
                studentRepository
        ).save(
                any(Student.class)
        );
    }

    @Test
    void addStudentWithDuplicateNumberShouldThrowException() {

        StudentRequest request =
                new StudentRequest(
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "REST API development",
                        true
                );

        when(
                studentRepository.existsByStudentNumber(
                        "20230001"
                )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> studentService.addStudent(
                                request
                        )
                );

        assertEquals(
                "Member number is already registered.",
                exception.getMessage()
        );

        verify(
                studentRepository,
                never()
        ).save(
                any(Student.class)
        );
    }

    @Test
    void getStudentByIdWhenStudentDoesNotExistShouldThrowException() {

        String studentId =
                "missing-student-id";

        when(
                studentRepository.findById(studentId)
        ).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> studentService
                                .getStudentById(studentId)
                );

        assertEquals(
                "Team member not found. ID: "
                        + studentId,
                exception.getMessage()
        );

        verify(
                studentRepository
        ).findById(studentId);
    }

    @Test
    void deleteStudentShouldCleanRelatedData() {

        String studentId =
                "student-id-1";

        String photoPath =
                "/api/media/files/students/photo.jpg";

        Student student =
                createStudent(
                        studentId,
                        photoPath
                );

        Set<String> projectStudentIds =
                new HashSet<>(
                        Set.of(
                                studentId,
                                "student-id-2"
                        )
                );

        Project project =
                new Project();

        project.setStudentIds(
                projectStudentIds
        );

        Set<String> publicationStudentIds =
                new HashSet<>(
                        Set.of(
                                studentId,
                                "student-id-3"
                        )
                );

        Publication publication =
                new Publication();

        publication.setStudentIds(
                publicationStudentIds
        );

        when(
                studentRepository.findById(studentId)
        ).thenReturn(
                Optional.of(student)
        );

        when(
                activityRepository.deleteByStudentId(
                        studentId
                )
        ).thenReturn(2L);

        when(
                projectRepository.findByStudentId(
                        studentId
                )
        ).thenReturn(
                List.of(project)
        );

        when(
                publicationRepository
                        .findByStudentIdsContaining(
                                studentId
                        )
        ).thenReturn(
                List.of(publication)
        );

        studentService.deleteStudent(studentId);

        /*
         * Öğrenci proje ve yayın bağlantılarından
         * çıkarılmış olmalı.
         */
        assertFalse(
                project.getStudentIds()
                        .contains(studentId)
        );

        assertFalse(
                publication.getStudentIds()
                        .contains(studentId)
        );

        /*
         * Temizleme işlemlerinin doğru sırada
         * çağrıldığını kontrol ediyoruz.
         */
        InOrder inOrder =
                inOrder(
                        activityRepository,
                        projectRepository,
                        publicationRepository,
                        studentRepository,
                        fileStorageService
                );

        inOrder.verify(
                activityRepository
        ).deleteByStudentId(studentId);

        inOrder.verify(
                projectRepository
        ).findByStudentId(studentId);

        inOrder.verify(
                projectRepository
        ).saveAll(
                any()
        );

        inOrder.verify(
                publicationRepository
        ).findByStudentIdsContaining(
                studentId
        );

        inOrder.verify(
                publicationRepository
        ).saveAll(
                any()
        );

        inOrder.verify(
                studentRepository
        ).delete(student);

        inOrder.verify(
                fileStorageService
        ).deleteImageByUrl(photoPath);
    }

    @Test
    void getStudentProfileShouldReturnRelatedDataAndStatistics() {

        String studentId = "student-id-1";

        Student student =
                createStudent(studentId, null);

        Project project = new Project();
        project.setId("project-id-1");
        project.setName("Network Monitoring");
        project.setSummary("Network monitoring project");
        project.setStudentIds(
                new HashSet<>(Set.of(studentId))
        );

        Publication publication = new Publication();
        publication.setId("publication-id-1");
        publication.setTitle("Network Analysis");
        publication.setAuthors(List.of("Ali Yilmaz"));
        publication.setVenue("Example Journal");
        publication.setPublicationYear(2026);
        publication.setStudentIds(
                new HashSet<>(Set.of(studentId))
        );

        Activity activity = new Activity();
        activity.setId("activity-id-1");
        activity.setStudentId(studentId);
        activity.setTitle("API Development");
        activity.setDescription("Developed profile endpoint");
        activity.setActivityDate(
                LocalDateTime.of(2026, 7, 7, 12, 0)
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        when(projectRepository.findByStudentId(studentId))
                .thenReturn(List.of(project));

        when(
                publicationRepository
                        .findByStudentIdsContaining(studentId)
        ).thenReturn(List.of(publication));

        when(
                activityRepository
                        .findByStudentIdOrderByActivityDateDesc(studentId)
        ).thenReturn(List.of(activity));

        StudentProfileResponse profile =
                studentService.getStudentProfile(studentId);

        assertEquals(studentId, profile.student().id());
        assertEquals(1, profile.statistics().projectCount());
        assertEquals(1, profile.statistics().publicationCount());
        assertEquals(1, profile.statistics().activityCount());
        assertEquals(3, profile.statistics().totalContributionCount());
        assertEquals("project-id-1", profile.projects().get(0).id());
        assertEquals(
                "publication-id-1",
                profile.publications().get(0).id()
        );
        assertEquals(
                "activity-id-1",
                profile.activities().get(0).id()
        );
    }

    private Student createStudent(
            String id,
            String photoPath
    ) {
        Student student =
                new Student();

        student.setId(id);
        student.setStudentNumber(
                "20230001"
        );
        student.setFirstName(
                "Ali"
        );
        student.setLastName(
                "Yilmaz"
        );
        student.setEmail(
                "ali@example.com"
        );
        student.setDepartment(
                "Computer Engineering"
        );
        student.setCurrentTask(
                "REST API development"
        );
        student.setPhotoPath(
                photoPath
        );
        student.setActive(true);

        return student;
    }
}
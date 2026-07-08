package com.example.demo.Controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.NewsPostResponse;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.dto.StudentResponse;
import com.example.demo.entity.NewsCategory;
import com.example.demo.entity.ProjectStatus;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.NewsPostService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MediaController.class,
        properties = {
                "app.security.admin.username=admin",
                "app.security.admin.password=Admin123!"
        }
)
@Import(SecurityConfig.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private NewsPostService newsPostService;

    @Test
    void uploadStudentPhotoWithoutAuthenticationShouldReturn401()
            throws Exception {

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "student-photo.jpg",
                        "image/jpeg",
                        new byte[]{1, 2, 3}
                );

        mockMvc.perform(
                        multipart(
                                "/api/media/students/{studentId}/photo",
                                "student-id-1"
                        ).file(image)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/media/students/student-id-1/photo"
                                )
                );

        verifyNoInteractions(
                studentService,
                fileStorageService
        );
    }

    @Test
    void uploadStudentPhotoWithAdminShouldUpdatePathAndDeleteOldPhoto()
            throws Exception {

        String oldPhotoPath =
                "/api/media/files/students/old-photo.jpg";

        String relativePath =
                "students/new-photo.jpg";

        String newPhotoPath =
                "/api/media/files/" + relativePath;

        StudentResponse existingStudent =
                new StudentResponse(
                        "student-id-1",
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "API development",
                        oldPhotoPath,
                        true
                );

        StudentResponse updatedStudent =
                new StudentResponse(
                        "student-id-1",
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "API development",
                        newPhotoPath,
                        true
                );

        given(
                studentService.getStudentById(
                        "student-id-1"
                )
        ).willReturn(existingStudent);

        given(
                fileStorageService.storeImage(
                        any(),
                        eq("students")
                )
        ).willReturn(relativePath);

        given(
                studentService.updatePhotoPath(
                        "student-id-1",
                        newPhotoPath
                )
        ).willReturn(updatedStudent);

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "student-photo.jpg",
                        "image/jpeg",
                        new byte[]{1, 2, 3}
                );

        mockMvc.perform(
                        multipart(
                                "/api/media/students/{studentId}/photo",
                                "student-id-1"
                        )
                                .file(image)
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value("student-id-1")
                )
                .andExpect(
                        jsonPath("$.photoPath")
                                .value(newPhotoPath)
                );

        verify(fileStorageService).storeImage(
                any(),
                eq("students")
        );

        verify(studentService).updatePhotoPath(
                "student-id-1",
                newPhotoPath
        );

        verify(fileStorageService).deleteImageByUrl(
                oldPhotoPath
        );
    }

    @Test
    void uploadProjectImageWithAdminShouldUpdatePathAndDeleteOldImage()
            throws Exception {

        String oldImagePath =
                "/api/media/files/projects/old-project.jpg";

        String relativePath =
                "projects/new-project.png";

        String newImagePath =
                "/api/media/files/" + relativePath;

        ProjectResponse existingProject =
                createProjectResponse(
                        oldImagePath
                );

        ProjectResponse updatedProject =
                createProjectResponse(
                        newImagePath
                );

        given(
                projectService.getProjectById(
                        "project-id-1"
                )
        ).willReturn(existingProject);

        given(
                fileStorageService.storeImage(
                        any(),
                        eq("projects")
                )
        ).willReturn(relativePath);

        given(
                projectService.updateImagePath(
                        "project-id-1",
                        newImagePath
                )
        ).willReturn(updatedProject);

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "project-image.png",
                        "image/png",
                        new byte[]{5, 10, 15}
                );

        mockMvc.perform(
                        multipart(
                                "/api/media/projects/{projectId}/image",
                                "project-id-1"
                        )
                                .file(image)
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value("project-id-1")
                )
                .andExpect(
                        jsonPath("$.imagePath")
                                .value(newImagePath)
                );

        verify(fileStorageService).storeImage(
                any(),
                eq("projects")
        );

        verify(projectService).updateImagePath(
                "project-id-1",
                newImagePath
        );

        verify(fileStorageService).deleteImageByUrl(
                oldImagePath
        );
    }

    @Test
    void uploadNewsImageWithAdminShouldUpdatePathAndDeleteOldImage()
            throws Exception {

        String oldImagePath =
                "/api/media/files/news/old-news.webp";

        String relativePath =
                "news/new-news.webp";

        String newImagePath =
                "/api/media/files/" + relativePath;

        NewsPostResponse existingNews =
                createNewsResponse(
                        oldImagePath
                );

        NewsPostResponse updatedNews =
                createNewsResponse(
                        newImagePath
                );

        given(
                newsPostService.getNewsPostById(
                        "news-id-1"
                )
        ).willReturn(existingNews);

        given(
                fileStorageService.storeImage(
                        any(),
                        eq("news")
                )
        ).willReturn(relativePath);

        given(
                newsPostService.updateImagePath(
                        "news-id-1",
                        newImagePath
                )
        ).willReturn(updatedNews);

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "news-image.webp",
                        "image/webp",
                        new byte[]{20, 30, 40}
                );

        mockMvc.perform(
                        multipart(
                                "/api/media/news/{newsId}/image",
                                "news-id-1"
                        )
                                .file(image)
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value("news-id-1")
                )
                .andExpect(
                        jsonPath("$.imagePath")
                                .value(newImagePath)
                );

        verify(fileStorageService).storeImage(
                any(),
                eq("news")
        );

        verify(newsPostService).updateImagePath(
                "news-id-1",
                newImagePath
        );

        verify(fileStorageService).deleteImageByUrl(
                oldImagePath
        );
    }

    private ProjectResponse createProjectResponse(
            String imagePath
    ) {
        return new ProjectResponse(
                "project-id-1",
                "Smart Laboratory",
                "Laboratory monitoring project.",
                "Sensor monitoring system.",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 9, 1),
                ProjectStatus.IN_PROGRESS,
                List.of(
                        "Java",
                        "Spring Boot"
                ),
                Set.of("student-id-1"),
                imagePath,
                "https://example.com/project",
                true
        );
    }

    private NewsPostResponse createNewsResponse(
            String imagePath
    ) {
        return new NewsPostResponse(
                "news-id-1",
                "New Project",
                "A new laboratory project started.",
                "Project details.",
                NewsCategory.PROJECT_UPDATE,
                imagePath,
                LocalDateTime.of(
                        2026,
                        7,
                        6,
                        18,
                        0
                ),
                true,
                true,
                "project-id-1"
        );
    }
}

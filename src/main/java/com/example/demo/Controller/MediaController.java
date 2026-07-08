package com.example.demo.Controller;

import com.example.demo.entity.NewsPost;
import com.example.demo.entity.Project;
import com.example.demo.entity.Student;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.NewsPostService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.StudentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.StudentResponse;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.dto.NewsPostResponse;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final FileStorageService fileStorageService;
    private final StudentService studentService;
    private final ProjectService projectService;
    private final NewsPostService newsPostService;

    public MediaController(
            FileStorageService fileStorageService,
            StudentService studentService,
            ProjectService projectService,
            NewsPostService newsPostService
    ) {
        this.fileStorageService = fileStorageService;
        this.studentService = studentService;
        this.projectService = projectService;
        this.newsPostService = newsPostService;
    }

    @PostMapping(
            value = "/students/{studentId}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public StudentResponse uploadStudentPhoto(
            @PathVariable String studentId,
            @RequestParam("file") MultipartFile file
    ) {
        StudentResponse student =
                studentService.getStudentById(studentId);

        String oldPhotoPath =
                student.photoPath();

        String relativePath =
                fileStorageService.storeImage(
                        file,
                        "students"
                );

        String newPhotoUrl =
                "/api/media/files/" + relativePath;

        StudentResponse updatedStudent =
                studentService.updatePhotoPath(
                        studentId,
                        newPhotoUrl
                );

        fileStorageService.deleteImageByUrl(
                oldPhotoPath
        );

        return updatedStudent;
    }

    @PostMapping(
            value = "/projects/{projectId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProjectResponse uploadProjectImage(
            @PathVariable String projectId,
            @RequestParam("file") MultipartFile file
    ) {
        ProjectResponse project =
                projectService.getProjectById(projectId);

        String oldImagePath =
                project.imagePath();

        String relativePath =
                fileStorageService.storeImage(
                        file,
                        "projects"
                );

        String newImageUrl =
                "/api/media/files/" + relativePath;

        ProjectResponse updatedProject =
                projectService.updateImagePath(
                        projectId,
                        newImageUrl
                );

        fileStorageService.deleteImageByUrl(
                oldImagePath
        );

        return updatedProject;
    }

    @PostMapping(
            value = "/news/{newsId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public NewsPostResponse uploadNewsImage(
            @PathVariable String newsId,
            @RequestParam("file") MultipartFile file
    ) {
        NewsPostResponse newsPost =
                newsPostService.getNewsPostById(newsId);

        String oldImagePath =
                newsPost.imagePath();

        String relativePath =
                fileStorageService.storeImage(
                        file,
                        "news"
                );

        String newImageUrl =
                "/api/media/files/" + relativePath;

        NewsPostResponse updatedNewsPost =
                newsPostService.updateImagePath(
                        newsId,
                        newImageUrl
                );

        fileStorageService.deleteImageByUrl(
                oldImagePath
        );

        return updatedNewsPost;
    }

    @GetMapping("/files/{folder}/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String folder,
            @PathVariable String filename
    ) {
        Resource resource =
                fileStorageService.loadImage(
                        folder,
                        filename
                );

        MediaType mediaType =
                MediaTypeFactory
                        .getMediaType(resource)
                        .orElse(
                                MediaType.APPLICATION_OCTET_STREAM
                        );

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + resource.getFilename()
                                + "\""
                )
                .body(resource);
    }
}
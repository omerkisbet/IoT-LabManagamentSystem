package com.example.demo.Controller;

import com.example.demo.dto.ProjectRequest;
import com.example.demo.dto.ProjectResponse;
import com.example.demo.entity.ProjectStatus;
import com.example.demo.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(
            ProjectService projectService
    ) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/featured")
    public List<ProjectResponse> getFeaturedProjects() {
        return projectService.getFeaturedProjects();
    }

    @GetMapping("/status/{status}")
    public List<ProjectResponse> getProjectsByStatus(
            @PathVariable ProjectStatus status
    ) {
        return projectService.getProjectsByStatus(status);
    }

    @GetMapping("/student/{studentId}")
    public List<ProjectResponse> getProjectsByStudent(
            @PathVariable String studentId
    ) {
        return projectService
                .getProjectsByStudentId(studentId);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(
            @PathVariable String id
    ) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.createProject(request);
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.updateProject(
                id,
                request
        );
    }

    @PatchMapping("/{projectId}/students/{studentId}")
    public ProjectResponse addStudentToProject(
            @PathVariable String projectId,
            @PathVariable String studentId
    ) {
        return projectService.addStudentToProject(
                projectId,
                studentId
        );
    }

    @DeleteMapping("/{projectId}/students/{studentId}")
    public ProjectResponse removeStudentFromProject(
            @PathVariable String projectId,
            @PathVariable String studentId
    ) {
        return projectService.removeStudentFromProject(
                projectId,
                studentId
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @PathVariable String id
    ) {
        projectService.deleteProject(id);
    }
}
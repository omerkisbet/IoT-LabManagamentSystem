package com.example.demo.repository;

import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectRepository
        extends MongoRepository<Project, String> {

    List<Project> findAllByOrderByStartDateDesc();

    List<Project> findByStatusOrderByStartDateDesc(
            ProjectStatus status
    );

    List<Project> findByFeaturedTrueOrderByStartDateDesc();

    @Query("{ 'studentIds': ?0 }")
    List<Project> findByStudentId(String studentId);
}
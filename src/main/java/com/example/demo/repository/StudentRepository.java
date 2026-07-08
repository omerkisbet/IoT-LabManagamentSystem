package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository
        extends MongoRepository<Student, String> {

    boolean existsByStudentNumber(String studentNumber);

    Optional<Student> findByStudentNumber(String studentNumber);

    List<Student> findByDepartmentIgnoreCase(String department);

    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );
}
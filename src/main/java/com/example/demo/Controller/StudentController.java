package com.example.demo.Controller;

import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.dto.StudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(
            StudentService studentService
    ) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}/profile")
    public StudentProfileResponse getStudentProfile(
            @PathVariable String id
    ) {
        return studentService.getStudentProfile(id);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(
            @PathVariable String id
    ) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse addStudent(
            @Valid @RequestBody StudentRequest request
    ) {
        return studentService.addStudent(request);
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequest request
    ) {
        return studentService.updateStudent(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(
            @PathVariable String id
    ) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/number/{studentNumber}")
    public StudentResponse getStudentByStudentNumber(
            @PathVariable String studentNumber
    ) {
        return studentService
                .getStudentByStudentNumber(studentNumber);
    }

    @GetMapping("/department/{department}")
    public List<StudentResponse> getStudentsByDepartment(
            @PathVariable String department
    ) {
        return studentService
                .getStudentsByDepartment(department);
    }

    @GetMapping("/search")
    public List<StudentResponse> searchStudents(
            @RequestParam String keyword
    ) {
        return studentService.searchStudents(keyword);
    }
}
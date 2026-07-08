package com.example.demo.Controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.dto.StudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.dto.StudentStatisticsResponse;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StudentController.class,
        properties = {
                "app.security.admin.username=admin",
                "app.security.admin.password=Admin123!"
        }
)
@Import(SecurityConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void getAllStudentsShouldBePublic()
            throws Exception {

        StudentResponse student =
                new StudentResponse(
                        "student-id-1",
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "REST API development",
                        null,
                        true
                );

        given(studentService.getAllStudents())
                .willReturn(List.of(student));

        mockMvc.perform(
                        get("/api/students")
                                .accept(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value("student-id-1")
                )
                .andExpect(
                        jsonPath("$[0].studentNumber")
                                .value("20230001")
                )
                .andExpect(
                        jsonPath("$[0].firstName")
                                .value("Ali")
                )
                .andExpect(
                        jsonPath("$[0].active")
                                .value(true)
                );

        verify(studentService).getAllStudents();
    }

    @Test
    void getStudentProfileShouldBePublic()
            throws Exception {

        StudentResponse student =
                new StudentResponse(
                        "student-id-1",
                        "20230001",
                        "Ali",
                        "Yilmaz",
                        "ali@example.com",
                        "Computer Engineering",
                        "REST API development",
                        null,
                        true
                );

        StudentProfileResponse profile =
                new StudentProfileResponse(
                        student,
                        new StudentStatisticsResponse(
                                2,
                                1,
                                3,
                                6
                        ),
                        List.of(),
                        List.of(),
                        List.of()
                );

        given(
                studentService.getStudentProfile(
                        "student-id-1"
                )
        ).willReturn(profile);

        mockMvc.perform(
                        get(
                                "/api/students/student-id-1/profile"
                        ).accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.student.id")
                                .value("student-id-1")
                )
                .andExpect(
                        jsonPath("$.statistics.projectCount")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.statistics.totalContributionCount")
                                .value(6)
                );

        verify(studentService).getStudentProfile(
                "student-id-1"
        );
    }

    @Test
    void createStudentWithoutAuthenticationShouldReturn401()
            throws Exception {

        mockMvc.perform(
                        post("/api/students")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStudentJson())
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
                        jsonPath("$.message")
                                .value(
                                        "Authentication is required."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/students")
                );

        verifyNoInteractions(studentService);
    }

    @Test
    void createStudentWithAdminShouldReturn201()
            throws Exception {

        StudentResponse createdStudent =
                new StudentResponse(
                        "student-id-2",
                        "20230002",
                        "Ayse",
                        "Demir",
                        "ayse@example.com",
                        "Software Engineering",
                        "Testing REST endpoints",
                        null,
                        true
                );

        given(
                studentService.addStudent(
                        any(StudentRequest.class)
                )
        ).willReturn(createdStudent);

        mockMvc.perform(
                        post("/api/students")
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStudentJson())
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value("student-id-2")
                )
                .andExpect(
                        jsonPath("$.studentNumber")
                                .value("20230002")
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Ayse")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );

        verify(studentService).addStudent(
                any(StudentRequest.class)
        );
    }

    @Test
    void createStudentWithInvalidBodyShouldReturn400()
            throws Exception {

        String invalidJson = """
                {
                  "studentNumber": "",
                  "firstName": "",
                  "lastName": "Celik",
                  "email": "invalid-email",
                  "department": "Computer Engineering",
                  "currentTask": "Testing",
                  "active": true
                }
                """;

        mockMvc.perform(
                        post("/api/students")
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    private String validStudentJson() {
        return """
                {
                  "studentNumber": "20230002",
                  "firstName": "Ayse",
                  "lastName": "Demir",
                  "email": "ayse@example.com",
                  "department": "Software Engineering",
                  "currentTask": "Testing REST endpoints",
                  "active": true
                }
                """;
    }
}
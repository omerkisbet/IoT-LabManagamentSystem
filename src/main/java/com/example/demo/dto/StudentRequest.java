package com.example.demo.dto;

import com.example.demo.entity.MemberType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Member number cannot be empty.")
        @Size(max = 50, message = "Member number cannot exceed 50 characters.")
        String studentNumber,

        @NotBlank(message = "First name cannot be empty.")
        @Size(max = 100, message = "First name cannot exceed 100 characters.")
        String firstName,

        @NotBlank(message = "Last name cannot be empty.")
        @Size(max = 100, message = "Last name cannot exceed 100 characters.")
        String lastName,

        @Email(message = "Please enter a valid email address.")
        @Size(max = 150, message = "Email cannot exceed 150 characters.")
        String email,

        @Size(max = 150, message = "Department cannot exceed 150 characters.")
        String department,

        @Size(max = 1000, message = "Current task cannot exceed 1000 characters.")
        String currentTask,

        Boolean active,

        MemberType memberType,

        @Size(max = 100, message = "Academic title cannot exceed 100 characters.")
        String academicTitle
) {
    public StudentRequest(
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String department,
            String currentTask,
            Boolean active
    ) {
        this(
                studentNumber,
                firstName,
                lastName,
                email,
                department,
                currentTask,
                active,
                MemberType.STUDENT,
                null
        );
    }
}

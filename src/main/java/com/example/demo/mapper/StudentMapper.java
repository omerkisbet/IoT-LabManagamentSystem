package com.example.demo.mapper;

import com.example.demo.dto.StudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.entity.MemberType;
import com.example.demo.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequest request) {
        Student student = new Student();

        student.setStudentNumber(request.studentNumber());
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setDepartment(request.department());
        student.setCurrentTask(request.currentTask());
        student.setMemberType(resolveMemberType(request.memberType()));
        student.setAcademicTitle(
                resolveMemberType(request.memberType()) == MemberType.ACADEMICIAN
                        ? normalizeOptionalText(request.academicTitle())
                        : null
        );

        student.setActive(
                request.active() == null
                        || request.active()
        );

        return student;
    }

    public static void updateEntity(
            Student student,
            StudentRequest request
    ) {
        MemberType memberType =
                resolveMemberType(request.memberType());

        student.setStudentNumber(request.studentNumber());
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setDepartment(request.department());
        student.setCurrentTask(request.currentTask());
        student.setMemberType(memberType);
        student.setAcademicTitle(
                memberType == MemberType.ACADEMICIAN
                        ? normalizeOptionalText(request.academicTitle())
                        : null
        );

        if (request.active() != null) {
            student.setActive(request.active());
        }
    }

    public static StudentResponse toResponse(Student student) {
        MemberType memberType =
                resolveMemberType(student.getMemberType());

        return new StudentResponse(
                student.getId(),
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDepartment(),
                student.getCurrentTask(),
                student.getPhotoPath(),
                student.isActive(),
                MongoTimestampUtil.resolveCreatedAt(
                        student.getId(),
                        student.getCreatedAt()
                ),
                student.getUpdatedAt(),
                memberType,
                memberType == MemberType.ACADEMICIAN
                        ? normalizeOptionalText(student.getAcademicTitle())
                        : null
        );
    }

    private static MemberType resolveMemberType(
            MemberType memberType
    ) {
        return memberType == null
                ? MemberType.STUDENT
                : memberType;
    }

    private static String normalizeOptionalText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}

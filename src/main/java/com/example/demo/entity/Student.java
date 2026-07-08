package com.example.demo.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "students")
public class Student {

    @Id
    private String id;

    @Indexed(unique = true)
    private String studentNumber;

    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String currentTask;
    private String photoPath;
    private boolean active = true;

    /*
     * Existing records created before v1.6 may not contain this field.
     * The mapper treats null values as STUDENT for backward compatibility.
     */
    private MemberType memberType = MemberType.STUDENT;

    private String academicTitle;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Student() {
    }

    public Student(
            String id,
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String department,
            String currentTask,
            String photoPath,
            boolean active
    ) {
        this(
                id,
                studentNumber,
                firstName,
                lastName,
                email,
                department,
                currentTask,
                photoPath,
                active,
                MemberType.STUDENT,
                null
        );
    }

    public Student(
            String id,
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String department,
            String currentTask,
            String photoPath,
            boolean active,
            MemberType memberType,
            String academicTitle
    ) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.currentTask = currentTask;
        this.photoPath = photoPath;
        this.active = active;
        this.memberType = memberType == null
                ? MemberType.STUDENT
                : memberType;
        this.academicTitle = academicTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType == null
                ? MemberType.STUDENT
                : memberType;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

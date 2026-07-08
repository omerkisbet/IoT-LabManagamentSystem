package com.example.demo.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "contact_messages")
public class ContactMessage {

    @Id
    private String id;

    @NotBlank(message = "Sender name cannot be empty.")
    @Size(max = 100, message = "Sender name cannot exceed 100 characters.")
    private String senderName;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Subject cannot be empty.")
    @Size(max = 200, message = "Subject cannot exceed 200 characters.")
    private String subject;

    @NotBlank(message = "Message cannot be empty.")
    @Size(
            max = 5000,
            message = "Message cannot exceed 5000 characters."
    )
    private String message;

    private LocalDateTime sentAt;

    private ContactStatus status = ContactStatus.NEW;

    private LocalDateTime repliedAt;

    private String replySubject;

    private String replyBody;

    public ContactMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(LocalDateTime repliedAt) {
        this.repliedAt = repliedAt;
    }

    public String getReplySubject() {
        return replySubject;
    }

    public void setReplySubject(String replySubject) {
        this.replySubject = replySubject;
    }

    public String getReplyBody() {
        return replyBody;
    }

    public void setReplyBody(String replyBody) {
        this.replyBody = replyBody;
    }
}
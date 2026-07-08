package com.example.demo.repository;

import com.example.demo.entity.ContactMessage;
import com.example.demo.entity.ContactStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactMessageRepository
        extends MongoRepository<ContactMessage, String> {

    List<ContactMessage> findAllByOrderBySentAtDesc();

    List<ContactMessage> findByStatusOrderBySentAtDesc(
            ContactStatus status
    );

    List<ContactMessage>
    findBySenderNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrderBySentAtDesc(
            String senderName,
            String email,
            String subject
    );

    long countByStatus(ContactStatus status);
}
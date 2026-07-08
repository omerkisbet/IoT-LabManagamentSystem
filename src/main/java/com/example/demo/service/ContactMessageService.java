package com.example.demo.service;

import com.example.demo.dto.ContactMessageReplyRequest;
import com.example.demo.dto.ContactMessageRequest;
import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.entity.ContactMessage;
import com.example.demo.entity.ContactStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ContactMessageMapper;
import com.example.demo.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository
            contactMessageRepository;

    private final EmailReplyService
            emailReplyService;

    public ContactMessageService(
            ContactMessageRepository contactMessageRepository,
            EmailReplyService emailReplyService
    ) {
        this.contactMessageRepository =
                contactMessageRepository;
        this.emailReplyService = emailReplyService;
    }

    public ContactMessageResponse createMessage(
            ContactMessageRequest request
    ) {
        ContactMessage contactMessage =
                ContactMessageMapper.toEntity(request);

        contactMessage.setId(null);
        contactMessage.setSentAt(LocalDateTime.now());
        contactMessage.setStatus(ContactStatus.NEW);

        ContactMessage savedMessage =
                contactMessageRepository.save(
                        contactMessage
                );

        return ContactMessageMapper.toResponse(
                savedMessage
        );
    }

    public List<ContactMessageResponse> getAllMessages() {
        return contactMessageRepository
                .findAllByOrderBySentAtDesc()
                .stream()
                .map(ContactMessageMapper::toResponse)
                .toList();
    }

    public ContactMessageResponse getMessageById(
            String id
    ) {
        return ContactMessageMapper.toResponse(
                findMessageEntityById(id)
        );
    }

    public List<ContactMessageResponse> getMessagesByStatus(
            ContactStatus status
    ) {
        return contactMessageRepository
                .findByStatusOrderBySentAtDesc(status)
                .stream()
                .map(ContactMessageMapper::toResponse)
                .toList();
    }

    public ContactMessageResponse changeMessageStatus(
            String id,
            ContactStatus status
    ) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Message status cannot be empty."
            );
        }

        ContactMessage contactMessage =
                findMessageEntityById(id);

        contactMessage.setStatus(status);

        ContactMessage savedMessage =
                contactMessageRepository.save(
                        contactMessage
                );

        return ContactMessageMapper.toResponse(
                savedMessage
        );
    }

    public ContactMessageResponse markAsRead(
            String id
    ) {
        ContactMessage contactMessage =
                findMessageEntityById(id);

        /*
         * Cevaplanmış veya arşivlenmiş mesajların
         * durumunu tekrar READ yapmıyoruz.
         */
        if (contactMessage.getStatus()
                == ContactStatus.NEW) {

            contactMessage.setStatus(
                    ContactStatus.READ
            );

            contactMessage =
                    contactMessageRepository.save(
                            contactMessage
                    );
        }

        return ContactMessageMapper.toResponse(
                contactMessage
        );
    }

    public List<ContactMessageResponse> searchMessages(
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        String normalizedKeyword =
                keyword.trim();

        return contactMessageRepository
                .findBySenderNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrderBySentAtDesc(
                        normalizedKeyword,
                        normalizedKeyword,
                        normalizedKeyword
                )
                .stream()
                .map(ContactMessageMapper::toResponse)
                .toList();
    }

    public long getNewMessageCount() {
        return contactMessageRepository
                .countByStatus(ContactStatus.NEW);
    }

    public ContactMessageResponse replyToMessage(
            String id,
            ContactMessageReplyRequest request
    ) {
        ContactMessage contactMessage =
                findMessageEntityById(id);

        emailReplyService.sendReply(
                contactMessage,
                request
        );

        contactMessage.setStatus(ContactStatus.REPLIED);
        contactMessage.setRepliedAt(LocalDateTime.now());
        contactMessage.setReplySubject(
                request.subject().trim()
        );
        contactMessage.setReplyBody(
                request.body().trim()
        );

        ContactMessage savedMessage =
                contactMessageRepository.save(
                        contactMessage
                );

        return ContactMessageMapper.toResponse(
                savedMessage
        );
    }

    public void deleteMessage(String id) {
        ContactMessage contactMessage =
                findMessageEntityById(id);

        contactMessageRepository.delete(
                contactMessage
        );
    }

    private ContactMessage findMessageEntityById(
            String id
    ) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact message not found. ID: "
                                        + id
                        )
                );
    }
}
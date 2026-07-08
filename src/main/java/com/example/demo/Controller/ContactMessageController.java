package com.example.demo.Controller;

import com.example.demo.dto.ContactMessageReplyRequest;
import com.example.demo.dto.ContactMessageRequest;
import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.entity.ContactStatus;
import com.example.demo.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageController {

    private final ContactMessageService
            contactMessageService;

    public ContactMessageController(
            ContactMessageService contactMessageService
    ) {
        this.contactMessageService =
                contactMessageService;
    }

    /*
     * Public endpoint:
     * Site ziyaretçisi mesaj gönderebilir.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactMessageResponse createMessage(
            @Valid
            @RequestBody
            ContactMessageRequest request
    ) {
        return contactMessageService
                .createMessage(request);
    }

    /*
     * Aşağıdaki endpoint'ler SecurityConfig nedeniyle
     * yalnızca ADMIN tarafından kullanılabilir.
     */
    @GetMapping
    public List<ContactMessageResponse>
    getAllMessages() {
        return contactMessageService
                .getAllMessages();
    }

    @GetMapping("/new-count")
    public Map<String, Long> getNewMessageCount() {
        return Map.of(
                "newMessageCount",
                contactMessageService
                        .getNewMessageCount()
        );
    }

    @GetMapping("/search")
    public List<ContactMessageResponse> searchMessages(
            @RequestParam String keyword
    ) {
        return contactMessageService
                .searchMessages(keyword);
    }

    @GetMapping("/status/{status}")
    public List<ContactMessageResponse>
    getMessagesByStatus(
            @PathVariable ContactStatus status
    ) {
        return contactMessageService
                .getMessagesByStatus(status);
    }

    @GetMapping("/{id}")
    public ContactMessageResponse getMessageById(
            @PathVariable String id
    ) {
        return contactMessageService
                .getMessageById(id);
    }

    @PatchMapping("/{id}/read")
    public ContactMessageResponse markAsRead(
            @PathVariable String id
    ) {
        return contactMessageService
                .markAsRead(id);
    }

    @PatchMapping("/{id}/status")
    public ContactMessageResponse changeMessageStatus(
            @PathVariable String id,
            @RequestParam ContactStatus status
    ) {
        return contactMessageService
                .changeMessageStatus(id, status);
    }

    @PostMapping("/{id}/reply")
    public ContactMessageResponse replyToMessage(
            @PathVariable String id,
            @Valid
            @RequestBody
            ContactMessageReplyRequest request
    ) {
        return contactMessageService
                .replyToMessage(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @PathVariable String id
    ) {
        contactMessageService
                .deleteMessage(id);
    }
}
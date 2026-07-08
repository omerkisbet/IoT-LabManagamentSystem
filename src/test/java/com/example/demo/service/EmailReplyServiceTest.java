package com.example.demo.service;

import com.example.demo.dto.ContactMessageReplyRequest;
import com.example.demo.entity.ContactMessage;
import com.example.demo.exception.EmailDeliveryException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailReplyServiceTest {

    @Test
    void completeSmtpConfigurationShouldSendEvenWhenLegacyFlagIsFalse() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage =
                new JavaMailSenderImpl().createMimeMessage();

        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        EmailReplyService service =
                new EmailReplyService(
                        mailSender,
                        false,
                        "lab@example.com",
                        "lab@example.com",
                        "app-password",
                        "smtp.example.com",
                        true
                );

        assertDoesNotThrow(() ->
                service.sendReply(
                        createMessage(),
                        createReplyRequest()
                )
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void missingAuthenticatedSmtpPasswordShouldReturnConfigurationError() {
        JavaMailSender mailSender = mock(JavaMailSender.class);

        EmailReplyService service =
                new EmailReplyService(
                        mailSender,
                        true,
                        "lab@example.com",
                        "lab@example.com",
                        "",
                        "smtp.example.com",
                        true
                );

        assertThrows(
                EmailDeliveryException.class,
                () -> service.sendReply(
                        createMessage(),
                        createReplyRequest()
                )
        );
    }

    private ContactMessage createMessage() {
        ContactMessage message = new ContactMessage();
        message.setSenderName("Test User");
        message.setEmail("recipient@example.com");
        message.setMessage("Original message");
        return message;
    }

    private ContactMessageReplyRequest createReplyRequest() {
        return new ContactMessageReplyRequest(
                "Re: Test subject",
                "Reply body"
        );
    }
}

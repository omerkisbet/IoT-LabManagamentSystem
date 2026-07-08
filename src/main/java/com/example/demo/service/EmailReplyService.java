package com.example.demo.service;

import com.example.demo.dto.ContactMessageReplyRequest;
import com.example.demo.entity.ContactMessage;
import com.example.demo.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailReplyService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailReplyService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String configuredFromAddress;
    private final String mailUsername;
    private final String mailPassword;
    private final String mailHost;
    private final boolean smtpAuthenticationEnabled;

    public EmailReplyService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}")
            boolean mailEnabled,
            @Value("${app.mail.from:}")
            String configuredFromAddress,
            @Value("${spring.mail.username:}")
            String mailUsername,
            @Value("${spring.mail.password:}")
            String mailPassword,
            @Value("${spring.mail.host:}")
            String mailHost,
            @Value("${spring.mail.properties.mail.smtp.auth:true}")
            boolean smtpAuthenticationEnabled
    ) {
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.configuredFromAddress = configuredFromAddress;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.mailHost = mailHost;
        this.smtpAuthenticationEnabled = smtpAuthenticationEnabled;
    }

    public void sendReply(
            ContactMessage contactMessage,
            ContactMessageReplyRequest request
    ) {
        validateConfiguration();

        String fromAddress = resolveFromAddress();

        try {
            MimeMessage mimeMessage =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            false,
                            StandardCharsets.UTF_8.name()
                    );

            helper.setFrom(fromAddress);
            helper.setTo(contactMessage.getEmail());
            helper.setSubject(
                    sanitizeSubject(request.subject())
            );
            helper.setText(
                    createEmailBody(contactMessage, request),
                    false
            );

            mailSender.send(mimeMessage);

        } catch (MessagingException | MailException exception) {
            throw new EmailDeliveryException(
                    "The reply email could not be sent. Check the SMTP host, username, app password and sender address.",
                    exception
            );
        }
    }

    private void validateConfiguration() {
        String fromAddress = resolveFromAddress();

        if (isBlank(mailHost) || isBlank(fromAddress)) {
            throw new EmailDeliveryException(
                    "SMTP settings are incomplete. Configure MAIL_HOST, MAIL_FROM and MAIL_USERNAME."
            );
        }

        if (
                smtpAuthenticationEnabled
                && (
                    isBlank(mailUsername)
                    || isBlank(mailPassword)
                )
        ) {
            throw new EmailDeliveryException(
                    "SMTP authentication settings are incomplete. Configure MAIL_USERNAME and MAIL_PASSWORD."
            );
        }

        /*
         * Older project versions generated .env files with
         * MAIL_ENABLED=false. A complete SMTP configuration is now enough
         * to activate sending, so that stale flag no longer blocks replies.
         */
        if (!mailEnabled) {
            LOGGER.info(
                    "MAIL_ENABLED is false, but complete SMTP settings are present. Email reply sending is enabled automatically."
            );
        }
    }

    private String resolveFromAddress() {
        if (!isBlank(configuredFromAddress)) {
            return configuredFromAddress.trim();
        }

        return isBlank(mailUsername)
                ? ""
                : mailUsername.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String sanitizeSubject(String subject) {
        return subject
                .trim()
                .replaceAll("[\\r\\n]+", " ");
    }

    private String createEmailBody(
            ContactMessage contactMessage,
            ContactMessageReplyRequest request
    ) {
        return request.body().trim()
                + "\n\n---\n"
                + "Original message from "
                + contactMessage.getSenderName()
                + ":\n"
                + contactMessage.getMessage();
    }
}

package com.example.demo.Controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.ContactMessageRequest;
import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.entity.ContactStatus;
import com.example.demo.service.ContactMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
        controllers = ContactMessageController.class,
        properties = {
                "app.security.admin.username=admin",
                "app.security.admin.password=Admin123!"
        }
)
@Import(SecurityConfig.class)
class ContactMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactMessageService contactMessageService;

    @Test
    void createMessageShouldBePublicAndReturn201()
            throws Exception {

        ContactMessageResponse response =
                new ContactMessageResponse(
                        "message-id-1",
                        "Omer Utku",
                        "utku@example.com",
                        "Laboratory internship",
                        "I want to get information about laboratory projects.",
                        LocalDateTime.of(
                                2026,
                                7,
                                6,
                                17,
                                30
                        ),
                        ContactStatus.NEW
                );

        given(
                contactMessageService.createMessage(
                        any(ContactMessageRequest.class)
                )
        ).willReturn(response);

        mockMvc.perform(
                        post("/api/contact-messages")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validMessageJson())
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value("message-id-1")
                )
                .andExpect(
                        jsonPath("$.senderName")
                                .value("Omer Utku")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("utku@example.com")
                )
                .andExpect(
                        jsonPath("$.subject")
                                .value("Laboratory internship")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("NEW")
                );

        verify(contactMessageService)
                .createMessage(
                        any(ContactMessageRequest.class)
                );
    }

    @Test
    void createMessageWithInvalidBodyShouldReturn400()
            throws Exception {

        String invalidJson = """
                {
                  "senderName": "",
                  "email": "invalid-email",
                  "subject": "",
                  "message": ""
                }
                """;

        mockMvc.perform(
                        post("/api/contact-messages")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(
                contactMessageService
        );
    }

    @Test
    void getAllMessagesWithoutAuthenticationShouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/api/contact-messages")
                                .accept(
                                        MediaType.APPLICATION_JSON
                                )
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
                                .value(
                                        "/api/contact-messages"
                                )
                );

        verifyNoInteractions(
                contactMessageService
        );
    }

    @Test
    void getAllMessagesWithAdminShouldReturn200()
            throws Exception {

        ContactMessageResponse response =
                new ContactMessageResponse(
                        "message-id-1",
                        "Omer Utku",
                        "utku@example.com",
                        "Laboratory internship",
                        "I want to get information about laboratory projects.",
                        LocalDateTime.of(
                                2026,
                                7,
                                6,
                                17,
                                30
                        ),
                        ContactStatus.NEW
                );

        given(
                contactMessageService.getAllMessages()
        ).willReturn(
                List.of(response)
        );

        mockMvc.perform(
                        get("/api/contact-messages")
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                                .accept(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value("message-id-1")
                )
                .andExpect(
                        jsonPath("$[0].senderName")
                                .value("Omer Utku")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("NEW")
                );

        verify(
                contactMessageService
        ).getAllMessages();
    }

    @Test
    void replyToMessageWithAdminShouldReturn200()
            throws Exception {

        ContactMessageResponse response =
                new ContactMessageResponse(
                        "message-id-1",
                        "Omer Utku",
                        "utku@example.com",
                        "Laboratory internship",
                        "I want to get information about laboratory projects.",
                        LocalDateTime.of(
                                2026,
                                7,
                                6,
                                17,
                                30
                        ),
                        ContactStatus.REPLIED
                );

        given(
                contactMessageService.replyToMessage(
                        org.mockito.ArgumentMatchers.eq("message-id-1"),
                        any(com.example.demo.dto.ContactMessageReplyRequest.class)
                )
        ).willReturn(response);

        mockMvc.perform(
                        post("/api/contact-messages/message-id-1/reply")
                                .with(
                                        httpBasic(
                                                "admin",
                                                "Admin123!"
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "subject": "Re: Laboratory internship",
                                          "body": "Thank you for your message."
                                        }
                                        """
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("REPLIED")
                );

        verify(contactMessageService)
                .replyToMessage(
                        org.mockito.ArgumentMatchers.eq("message-id-1"),
                        any(com.example.demo.dto.ContactMessageReplyRequest.class)
                );
    }

    private String validMessageJson() {
        return """
                {
                  "senderName": "Omer Utku",
                  "email": "utku@example.com",
                  "subject": "Laboratory internship",
                  "message": "I want to get information about laboratory projects."
                }
                """;
    }
}
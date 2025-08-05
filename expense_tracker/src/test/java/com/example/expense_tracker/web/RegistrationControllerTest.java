package com.example.expense_tracker.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    private GreenMail greenMail;

    @BeforeEach
    void setUp() {
        greenMail = new GreenMail(new ServerSetup(port, host, "smtp"));
        greenMail.start();
        greenMail.setUser(username, password);
    }

    @AfterEach
    void tearDown() {
        greenMail.stop();
    }


    @Test
    @Transactional
    @Rollback
    void testRegistration() throws Exception {

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "pesho@softuni.bg",
                                    "firstname": "Pesho",
                                    "lastname": "Petrov",
                                    "password": "topsecret",
                                    "confirmPassword": "topsecret",
                                    "userRoles": ["USER", "MODERATOR"]
                                }
                                """)
                        .with(csrf()))
                .andExpect(status().isOk());

        UserEntity savedUser = userRepository.findByEmail("pesho@softuni.bg").orElse(null);

        assertNotNull(savedUser, "User should be saved in database");
        assertEquals("Pesho", savedUser.getFirstname(), "First name should match");
        assertEquals("Petrov", savedUser.getLastname(), "Last name should match");
        assertEquals("pesho@softuni.bg", savedUser.getEmail(), "Email should match");

        assertTrue(passwordEncoder.matches("topsecret", savedUser.getPassword()),
                "Password should be encrypted correctly");

        assertEquals(2, savedUser.getRoles().size(), "User should have 2 roles");

        Set<String> roleNames = savedUser.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
        assertTrue(roleNames.contains("USER"), "Should have USER role");
        assertTrue(roleNames.contains("MODERATOR"), "Should have MODERATOR role");

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        Assertions.assertEquals(1, receivedMessages.length);
        MimeMessage registrationMessage = receivedMessages[0];

        Assertions.assertTrue(registrationMessage.getContent().toString().contains("Pesho"));
        Assertions.assertEquals(1, registrationMessage.getAllRecipients().length);
        Assertions.assertEquals("pesho@softuni.bg", registrationMessage.getAllRecipients()[0].toString());

    }

}

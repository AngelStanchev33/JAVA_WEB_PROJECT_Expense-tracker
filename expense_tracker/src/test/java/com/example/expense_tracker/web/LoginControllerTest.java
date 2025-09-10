package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.AuthRequestDto;
import com.example.expense_tracker.model.dto.AuthResponseDto;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.testUtil.UserTestDataUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    private static final String TEST_USER_EMAIL = "testUser@test.bg";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final List<UserRoleEnum> USER_ADMIN_ROLES = List.of(UserRoleEnum.USER, UserRoleEnum.ADMIN);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserTestDataUtil userTestDataUtil;

    @BeforeEach
    void setUp() {
        userTestDataUtil.cleanUp();
    }

    @AfterEach
    void tearDown() {
        userTestDataUtil.cleanUp();
    }

    @Test
    public void test_login_WithValidCredentials_ReturnsToken() throws Exception {
        userTestDataUtil.createUser(TEST_USER_EMAIL, USER_ADMIN_ROLES, TEST_USER_PASSWORD);

        AuthRequestDto request = new AuthRequestDto(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequestDto> requestDtoHttpEntity
                = new HttpEntity<>(request, headers);

        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                requestDtoHttpEntity,
                AuthResponseDto.class
        );


        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().token()).isNotEmpty();
    }

    @Test
    public void test_login_NotValidCredentials_Returns401() {
        AuthRequestDto request = new AuthRequestDto(TEST_USER_EMAIL, "wrongPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequestDto> requestDtoHttpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                requestDtoHttpEntity,
                AuthResponseDto.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}



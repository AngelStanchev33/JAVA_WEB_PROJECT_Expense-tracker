package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.AuthRequestDto;
import com.example.expense_tracker.model.dto.AuthResponseDto;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.repository.UserRoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

// ИНТЕГРАЦИОНЕН ТЕСТ - стартира цялото Spring Boot приложение на random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    // Autowired TestRestTemplate - за правене на HTTP заявки към приложението
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    public void login_WithValidCredentials_ReturnsToken() throws Exception {
        // 1. ПОДГОТОВКА - създаваме DTO с тестови credentials
        AuthRequestDto request = new AuthRequestDto("testUser@test.bg", "password123");

        // 2. HTTP HEADERS - задаваме Content-Type: application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. HTTP ENTITY - комбинираме request body + headers
        HttpEntity<AuthRequestDto> requestDtoHttpEntity = new HttpEntity<>(request, headers);

        // 4. HTTP ЗАЯВКА - POST към /auth/login endpoint-а
        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(
                "/auth/login",               // URL endpoint
                HttpMethod.POST,             // HTTP метод
                requestDtoHttpEntity,        // Request body + headers
                AuthResponseDto.class        // Очакван response тип
        );

        System.out.println();

        // 5. ASSERTION-И - проверяваме отговора
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);  // HTTP 200
        Assertions.assertThat(response.getBody()).isNotNull();                     // Body не е null
        Assertions.assertThat(response.getBody().token()).isNotEmpty();            // Token не е празен
    }

    @Test
    public void login_WithValidCredentials_Returns401() {
        AuthRequestDto request = new AuthRequestDto("testUser@test.bg", "password1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequestDto> requestDtoHttpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(
                "/auth/login",
                HttpMethod.POST,
                requestDtoHttpEntity,
                AuthResponseDto.class
        );


        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}



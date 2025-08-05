package com.example.expense_tracker.config;

import com.example.expense_tracker.service.JwtService;
import com.example.expense_tracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class OAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public OAuthSuccessHandler(UserService userService, JwtService jwtService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        if (authentication instanceof OAuth2AuthenticationToken token) {

            OAuth2User user = token.getPrincipal();

            String email = user.getAttribute("email");
            String name = user.getAttribute("name");
            String login = user.getAttribute("login");

            if (email == null) {
                email = login + "@github.local";
            }
            if (name == null) {
                name = login;
            }

            userService.registerWithOauth2(login, email);

            Authentication authResult = userService.loginWithOAuth(email);

            List<String> roles = authResult.getAuthorities().stream()
                    .map(Object::toString)
                    .toList();

            Map<String, Object> claims = Map.of("roles", roles);

            String jwtToken = jwtService.generateToken(email, claims);

            if (jwtService.isTokenValid(jwtToken)) {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, String> tokenResponse = Map.of("token", jwtToken);
                response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
            }
        }
    }
}

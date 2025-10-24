package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.CategoryNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.RegisterRequestDto;
import com.example.expense_tracker.model.dto.UserDto;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.repository.UserRoleRepository;
import com.example.expense_tracker.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final ExpenseTrackerUserDetailsService expenseTrackerUserDetailsService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserServiceImpl(ExpenseTrackerUserDetailsService expenseTrackerUserDetailsService,
                           UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.expenseTrackerUserDetailsService = expenseTrackerUserDetailsService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void register(RegisterRequestDto requestDto) {
        UserEntity user = modelMapper.map(requestDto, UserEntity.class);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(List.of(
                userRoleRepository.findByRoleName(UserRoleEnum.USER)
                        .orElseThrow(() -> new CategoryNotFoundException("USER")))
        );

        userRepository.save(user);
        publishUserRegisteredEvent(requestDto);
    }

    @Override
    public void registerWithOauth2(String login, String email) {

        if (userRepository.findByEmail(email).isEmpty()) {
            String[] nameParts = login.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : firstName;

            List<UserRoleEntity> userRole = userRoleRepository.findByRoleNameIn(List.of(UserRoleEnum.USER));

            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setActive(true);
            newUser.setPassword(passwordEncoder.encode("oauth2-dummy-password"));
            newUser.setRoles(userRole);

            userRepository.save(newUser);
            
            RegisterRequestDto dto = new RegisterRequestDto()
                    .setEmail(email)
                    .setFirstname(firstName);
            publishUserRegisteredEvent(dto);
        }
    }

    @Override
    public Authentication loginWithOAuth(String email) {
        UserDetails user = expenseTrackerUserDetailsService.loadUserByUsername(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }

    @Override
    public UserDto getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new UserDto(
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getImageUrl()
        );
    }

    private void publishUserRegisteredEvent(RegisterRequestDto requestDto) {
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(
                requestDto.getEmail(),
                requestDto.getFirstname()
        ));
    }
}
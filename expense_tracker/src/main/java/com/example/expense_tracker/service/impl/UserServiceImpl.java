package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.RegisterRequestDto;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
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
        List<UserRoleEntity> foundRoles = resolveUserRoles(requestDto.getUserRoles());
        UserEntity user = createUserFromRequest(requestDto, foundRoles);
        userRepository.save(user);
        publishUserRegisteredEvent(requestDto);
    }

    @Override
    public void createUserIfNotExist(String login, String email) {

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
        }
    }

    @Override
    public Authentication loginWithOAuth(String email) {
        System.out.println("trying to log in");
        UserDetails user = expenseTrackerUserDetailsService.loadUserByUsername(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }

    private List<UserRoleEntity> resolveUserRoles(List<String> roleNames) {
        List<UserRoleEnum> userRoles = roleNames.stream()
                .map(UserRoleEnum::valueOf)
                .toList();
        return userRoleRepository.findByRoleNameIn(userRoles);
    }

    private UserEntity createUserFromRequest(RegisterRequestDto requestDto, List<UserRoleEntity> roles) {
        UserEntity user = modelMapper.map(requestDto, UserEntity.class);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(roles);
        return user;
    }

    private void publishUserRegisteredEvent(RegisterRequestDto requestDto) {
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(
                requestDto.getEmail(),
                requestDto.getFirstname()
        ));
    }
}
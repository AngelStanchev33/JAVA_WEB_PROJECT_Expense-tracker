package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.RegisterRequestDto;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.repository.UserRoleRepository;
import com.example.expense_tracker.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher applicationEventPublisher;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher) {
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
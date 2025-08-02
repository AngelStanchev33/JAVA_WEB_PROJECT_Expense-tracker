package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.user.ExpenseTrackerUserDetails;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ExpenseTrackerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ExpenseTrackerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmailWithUserRoles(userEmail)
                .map(this::map)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + userEmail + "not found"));
    }

    private User map(UserEntity userEntity) {
        return new ExpenseTrackerUserDetails(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRoles().stream().map(role -> this.map(role.getRoleName())).toList(),
                userEntity.getFirstname(),
                userEntity.getLastname()
        );
    }

    private GrantedAuthority map(UserRoleEnum role) {
        return new SimpleGrantedAuthority("ROLE_" + role);
    }
}

package com.example.expense_tracker.service;

import static org.mockito.Mockito.when;

import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.user.ExpenseTrackerUserDetails;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.impl.ExpenseTrackerUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ExpenseTrackerUserDetailsServiceTest {

    private static final String TEST_EMAIL = "user@example.com";
    private static final String NOT_EXISTENT_EMAIL = "noone@example.com";
    private UserDetailsService testUserService;

    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    void SetUp() {
        testUserService = new ExpenseTrackerUserDetailsService(mockUserRepository);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        UserEntity testUser = new UserEntity();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("topsecret");
        testUser.setFirstname("John");
        testUser.setLastname("Doe");

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName(UserRoleEnum.USER);

        UserRoleEntity adminRole = new UserRoleEntity();
        adminRole.setRoleName(UserRoleEnum.ADMIN);

        testUser.setRoles(List.of(
                userRole, adminRole
        ));

        when(mockUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = testUserService.loadUserByUsername(TEST_EMAIL);

        ExpenseTrackerUserDetails expenseTrackerUser = (ExpenseTrackerUserDetails) userDetails;

        Assertions.assertInstanceOf(ExpenseTrackerUserDetails.class, userDetails);

        Assertions.assertEquals(testUser.getEmail(), expenseTrackerUser.getUsername());
        Assertions.assertEquals(testUser.getPassword(), expenseTrackerUser.getPassword());
        Assertions.assertEquals(testUser.getFirstname(), expenseTrackerUser.getFirstname());
        Assertions.assertEquals(testUser.getLastname(), expenseTrackerUser.getLastname());

        List<String> expectedRoles = testUser.getRoles()
                .stream()
                .map(UserRoleEntity::getRoleName)
                .map(r -> "ROLE_" + r).toList();

        List<String> actualRoles = expenseTrackerUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Assertions.assertEquals(expectedRoles.size(), actualRoles.size());
        Assertions.assertTrue(actualRoles.containsAll(expectedRoles));

    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(mockUserRepository.findByEmail(NOT_EXISTENT_EMAIL)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            testUserService.loadUserByUsername(NOT_EXISTENT_EMAIL);
        });
    }
}

package com.example.expense_tracker.testUtil;

import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserTestDataUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserEntity createUser(String email, List<UserRoleEnum> roles) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(true);
        user.setRoles(userRoleRepository.findByRoleNameIn(roles));

        userRepository.save(user);

        return user;

    }

    public UserEntity createUser(String email, List<UserRoleEnum> roles, String password) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setRoles(userRoleRepository.findByRoleNameIn(roles));

        userRepository.save(user);

        return user;

    }

    public void cleanUp() {
        userRepository.deleteAll();
    }
}

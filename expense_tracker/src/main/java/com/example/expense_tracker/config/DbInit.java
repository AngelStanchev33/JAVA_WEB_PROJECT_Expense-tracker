package com.example.expense_tracker.config;

import com.example.expense_tracker.model.entity.UserRoleEntity;

import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInit implements CommandLineRunner {

    private final UserRoleRepository userRoleRepository;

    public DbInit(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Roles inject");

        if (userRoleRepository.count() == 0) {
            UserRoleEntity admin = new UserRoleEntity();
            admin.setRoleName(UserRoleEnum.ADMIN);
            UserRoleEntity moderator = new UserRoleEntity();
            moderator.setRoleName(UserRoleEnum.MODERATOR);
            UserRoleEntity user = new UserRoleEntity();
            user.setRoleName(UserRoleEnum.USER);

            userRoleRepository.saveAll(List.of(admin, moderator, user));

        }
    }
}

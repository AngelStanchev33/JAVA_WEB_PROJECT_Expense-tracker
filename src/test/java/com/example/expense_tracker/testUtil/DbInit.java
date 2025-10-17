package com.example.expense_tracker.testUtil;

import com.example.expense_tracker.model.entity.CategoryEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.repository.CategoryRepository;
import com.example.expense_tracker.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInit implements CommandLineRunner {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRoleRepository.count() == 0) {
            UserRoleEntity user = new UserRoleEntity();
            UserRoleEntity admin = new UserRoleEntity();
            UserRoleEntity moderator = new UserRoleEntity();

            user.setRoleName(UserRoleEnum.USER);
            admin.setRoleName(UserRoleEnum.ADMIN);
            moderator.setRoleName(UserRoleEnum.MODERATOR);

            userRoleRepository.saveAll(List.of(user, admin, moderator));

        }

        if (categoryRepository.count() == 0) {

            for (int i = 0; i < CategoryEnum.values().length; i++) {
                CategoryEnum[] values = CategoryEnum.values();

                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setCategoryEnum(values[i]);
                categoryRepository.save(categoryEntity);
            }
        }
    }
}

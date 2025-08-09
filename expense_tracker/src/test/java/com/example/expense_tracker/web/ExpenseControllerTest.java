package com.example.expense_tracker.web;

import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.testUtil.TestDataUtil;
import com.example.expense_tracker.testUtil.UserTestDataUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExpenseControllerTest {

    private static final String TEST_USER1_EMAIL = "user1@example.com";
    private static final List<UserRoleEnum> USER_ROLES = List.of(UserRoleEnum.USER);
    private static final List<UserRoleEnum> ADMIN_ROLES = List.of(UserRoleEnum.USER, UserRoleEnum.ADMIN);


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserTestDataUtil userTestDataUtil;

    @Autowired
    private TestDataUtil testDataUtil;

    @BeforeEach
    void setUp() {
        testDataUtil.cleanUp();
        userTestDataUtil.cleanUp();
    }

    @AfterEach
    void dbCleanup() {
        testDataUtil.cleanUp();
        userTestDataUtil.cleanUp();
    }

    @Test
    void test_AnonymousDeletion_Returns_Unauthorized() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, ADMIN_ROLES);
        ExpenseEntity expense = testDataUtil.createExpense(owner, CategoryEnum.BILLS);

        mockMvc.perform(delete("/expenses/delete/{id}", expense.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_Owner_CanDelete_Expense() throws Exception {
        UserEntity owner = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        ExpenseEntity expense = testDataUtil.createExpense(owner, CategoryEnum.BILLS);

        mockMvc.perform(delete("/expenses/delete/{id}", expense.getId())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_NotOwner_CantDelete_Expense() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, ADMIN_ROLES);
        ExpenseEntity expense = testDataUtil.createExpense(owner, CategoryEnum.BILLS);

        mockMvc.perform(delete("/expenses/delete/{id}", expense.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

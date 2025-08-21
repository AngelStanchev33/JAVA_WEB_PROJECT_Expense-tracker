package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.testUtil.TestDataUtil;
import com.example.expense_tracker.testUtil.UserTestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void test_AnonymousCreate_Returns_Unauthorized() throws Exception {
        CreateExpenseDto createDto = createValidExpenseDto();

        mockMvc.perform(post("/expenses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_AuthenticatedUser_CanCreate_Expense() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        CreateExpenseDto createDto = createValidExpenseDto();

        mockMvc.perform(post("/expenses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Expense"))
                .andExpect(jsonPath("$.amount").value(100.50));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_CreateExpense_WithInvalidData_Returns_BadRequest() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        CreateExpenseDto invalidDto = new CreateExpenseDto()
                .setDescription("") // Invalid - blank
                .setAmount(new BigDecimal("-10")) // Invalid - negative
                .setCategory("INVALID_CATEGORY")
                .setDate(LocalDate.now().minusDays(1)); // Invalid - past date

        mockMvc.perform(post("/expenses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_AnonymousGetMyExpenses_Returns_Unauthorized() throws Exception {
        mockMvc.perform(get("/expenses/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_AuthenticatedUser_CanGetMyExpenses() throws Exception {
        UserEntity user = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        testDataUtil.createExpense(user, CategoryEnum.FOOD);
        testDataUtil.createExpense(user, CategoryEnum.BILLS);

        mockMvc.perform(get("/expenses/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_GetMyExpenses_ReturnsOnlyUserExpenses() throws Exception {
        // Create user and their expenses
        UserEntity user1 = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        UserEntity user2 = userTestDataUtil.createUser("other@test.bg", USER_ROLES);

        
        testDataUtil.createExpense(user1, CategoryEnum.FOOD);
        testDataUtil.createExpense(user2, CategoryEnum.BILLS); // Other user's expense

        mockMvc.perform(get("/expenses/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)); // Only user1's expense
    }

    private CreateExpenseDto createValidExpenseDto() {
        return new CreateExpenseDto()
                .setDescription("Test Expense")
                .setAmount(new BigDecimal("100.50"))
                .setCategory("FOOD")
                .setDate(LocalDate.now());
    }
}

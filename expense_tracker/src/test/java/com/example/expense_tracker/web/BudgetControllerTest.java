package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.entity.BudgetEntity;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetControllerTest {

    private static final String TEST_USER1_EMAIL = "user1@example.com";
    private static final List<UserRoleEnum> USER_ROLES = List.of(UserRoleEnum.USER);
    private static final List<UserRoleEnum> ADMIN_ROLES = List.of(UserRoleEnum.USER, UserRoleEnum.ADMIN);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataUtil testDataUtil;

    @Autowired
    private UserTestDataUtil userTestDataUtil;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testDataUtil.cleanUp();
        userTestDataUtil.cleanUp();
    }

    @AfterEach
    void tearDown() {
        testDataUtil.cleanUp();
        userTestDataUtil.cleanUp();
    }

    @Test
    void test_AnonymousDeletion_Returns_Unauthorized() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, ADMIN_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(delete("/budgets/{id}", budget.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_Owner_CanDelete_Budget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "EUR");

        mockMvc.perform(delete("/budgets/{id}", budget.getId())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_NotOwner_CantDelete_Budget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, ADMIN_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        mockMvc.perform(delete("/budgets/{id}", budget.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_AnonymousCreate_Returns_Unauthorized() throws Exception {
        CreateBudgetDto budgetDto = createValidBudgetDto();

        mockMvc.perform(post("/budgets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_AuthenticatedUser_CanCreate_Budget() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        CreateBudgetDto budgetDto = createValidBudgetDto();

        mockMvc.perform(post("/budgets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2024-08"))
                .andExpect(jsonPath("$.budgetLimit").value(1000.0))
                .andExpect(jsonPath("$.currencyCode").value("BGN"));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_CreateBudget_WithInvalidData_Returns_BadRequest() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        CreateBudgetDto invalidDto = new CreateBudgetDto()
                .setMonth("")                 .setBudgetLimit(new BigDecimal("-10")); 
        mockMvc.perform(post("/budgets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_AnonymousGetMyBudgets_Returns_Unauthorized() throws Exception {
        mockMvc.perform(get("/budgets/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_AuthenticatedUser_CanGetMyBudgets() throws Exception {
        UserEntity user = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        testDataUtil.createBudget(user, "2024-08", new BigDecimal("150.00"),
                new BigDecimal("1000.00"), "EUR");
        testDataUtil.createBudget(user, "2024-09", new BigDecimal("200.00"),
                new BigDecimal("1200.00"), "BGN");

        mockMvc.perform(get("/budgets/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_GetMyBudgets_ReturnsOnlyUserBudgets() throws Exception {
        UserEntity user1 = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        UserEntity user2 = userTestDataUtil.createUser("other@test.bg", USER_ROLES);

        testDataUtil.createBudget(user1, "2024-08", new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        testDataUtil.createBudget(user2, "2024-08", new BigDecimal("200.00"),
                new BigDecimal("1200.00"),
                "BGN"); 
        mockMvc.perform(get("/budgets/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));     }

    @Test
    void test_AnonymousGetBudgetById_Returns_Unauthorized() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(get("/budgets/{id}", budget.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_Owner_CanGetBudgetById() throws Exception {
        UserEntity owner = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(get("/budgets/{id}", budget.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(budget.getId()))
                .andExpect(jsonPath("$.month").value("2024-08"))
                .andExpect(jsonPath("$.budgetLimit").value(1000.0))
                .andExpect(jsonPath("$.spent").value(150.0))
                .andExpect(jsonPath("$.currencyCode").value("BGN"));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_NonOwner_CantGetBudgetById() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(get("/budgets/{id}", budget.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_GetNonExistentBudget_Returns_NotFound() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        Long nonExistentId = 99999L;

        mockMvc.perform(get("/budgets/{id}", nonExistentId))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_AnonymousUpdateBudget_Returns_Unauthorized() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        UpdateBudgetDto updateDto = createValidUpdateBudgetDto();

        mockMvc.perform(put("/budgets/{id}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_Owner_CanUpdateBudget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        UpdateBudgetDto updateDto = createValidUpdateBudgetDto();

        mockMvc.perform(put("/budgets/{id}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2024-12"))
                .andExpect(jsonPath("$.budgetLimit").value(1500.0))
                .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_NonOwner_CantUpdateBudget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        UpdateBudgetDto updateDto = createValidUpdateBudgetDto();

        mockMvc.perform(put("/budgets/{id}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_UpdateNonExistentBudget_Returns_Forbidden() throws Exception {
        userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        UpdateBudgetDto updateDto = createValidUpdateBudgetDto();
        Long nonExistentId = 99999L;

        mockMvc.perform(put("/budgets/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.bg", roles = "USER")
    void test_UpdateBudgetWithInvalidData_Returns_BadRequest() throws Exception {
        UserEntity owner = userTestDataUtil.createUser("test@test.bg", USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        
        UpdateBudgetDto invalidDto = new UpdateBudgetDto()
                .setMonth("")                 .setBudgetLimit(new BigDecimal("-100.00")); 
        mockMvc.perform(put("/budgets/{id}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    private CreateBudgetDto createValidBudgetDto() {
        return new CreateBudgetDto()
                .setMonth("2024-08")
                .setBudgetLimit(BigDecimal.valueOf(1000))
                .setCurrencyCode("BGN");
    }

    private UpdateBudgetDto createValidUpdateBudgetDto() {
        return new UpdateBudgetDto()
                .setMonth("2024-12")
                .setBudgetLimit(new BigDecimal("1500.00"))
                .setCurrency("EUR");
    }

        @Test
    @WithMockUser(username = "admin@test.bg", roles = {"USER", "ADMIN"})
    void test_Admin_CanDelete_AnyBudget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(delete("/budgets/{id}", budget.getId())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.bg", roles = {"USER", "ADMIN"})
    void test_Admin_CanUpdate_AnyBudget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");
        UpdateBudgetDto updateDto = createValidUpdateBudgetDto();

        mockMvc.perform(put("/budgets/{id}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2024-12"));
    }

    @Test
    @WithMockUser(username = "admin@test.bg", roles = {"USER", "ADMIN"})
    void test_Admin_CanGet_AnyBudget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(get("/budgets/{id}", budget.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(budget.getId()));
    }

    @Test
    @WithMockUser(username = "regular@test.bg", roles = "USER")
    void test_NonAdminNonOwner_CantDelete_Budget() throws Exception {
        UserEntity owner = userTestDataUtil.createUser(TEST_USER1_EMAIL, USER_ROLES);
        BudgetEntity budget = testDataUtil.createBudget(owner, "2024-08",
                new BigDecimal("150.00"),
                new BigDecimal("1000.00"),
                "BGN");

        mockMvc.perform(delete("/budgets/{id}", budget.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

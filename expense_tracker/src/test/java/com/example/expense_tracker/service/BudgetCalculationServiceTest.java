package com.example.expense_tracker.service;

import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.NotificationEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.NotificationRepository;
import com.example.expense_tracker.service.impl.BudgetCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BudgetCalculationServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private NotificationRepository notificationRepository;


    private BudgetCalculationService budgetCalculationService;

    @BeforeEach
    void setUp() {
        budgetCalculationService = new BudgetCalculationServiceImpl(budgetRepository, notificationRepository);
    }

    @Test
    void calculateBudget_Should_Send_Notification() {
        ExpenseCreatedEvent event = new ExpenseCreatedEvent(1L, "test@gmail.com",
                BigDecimal.valueOf(500), "2025-08");
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));

        when(budgetRepository.findByUserEmailAndAndMonth(event.getUserEmail(),
                event.getMonth())).thenReturn(Optional.of(budget));

        budgetCalculationService.calculateBudget(event.getUserEmail(), event.getExpenseId(),
                event.getMonth(), event.getAmount());

        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));

    }

    private BudgetEntity createBudget(BigDecimal limit, UserEntity owner, String month, BigDecimal spent) {
        BudgetEntity entity = new BudgetEntity();
        entity.setId(1L);
        entity.setBudgetLimit(limit);
        entity.setUser(owner);
        entity.setMonth(month);
        entity.setSpent(spent);

        return entity;
    }

    private UserEntity createUser() {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName(UserRoleEnum.USER);

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstname("Test");
        entity.setLastname("Testov");
        entity.setRoles(List.of(userRole));

        return entity;
    }


}

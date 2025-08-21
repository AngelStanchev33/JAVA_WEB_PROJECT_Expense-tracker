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
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import com.example.expense_tracker.model.enums.NotificationTypeEnum;

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
        // Arrange
        ExpenseCreatedEvent event = new ExpenseCreatedEvent(1L, "test@gmail.com",
                BigDecimal.valueOf(500), "2025-08");
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));

        when(budgetRepository.findByUserEmailAndAndMonth(event.getUserEmail(),
                event.getMonth())).thenReturn(Optional.of(budget));

        // Act
        budgetCalculationService.calculateBudget(event.getUserEmail(), event.getExpenseId(),
                event.getMonth(), event.getAmount());

        // Assert - използваме ArgumentCaptor за да "прихванем" какво се записва
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        // Сега можем да проверим съдържанието на записания обект
        NotificationEntity savedNotification = captor.getValue();
        assertEquals("💸 Halfway to broke! 50% budget gone", savedNotification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_WARNING_50, savedNotification.getType());
        assertEquals(owner.getId(), savedNotification.getUser().getId());
        assertEquals(budget.getId(), savedNotification.getRelatedBudgetId());
        assertEquals(event.getExpenseId(), savedNotification.getRelatedExpenseId());
    }

    @Test
    void calculateBudget_Should_DoNothing_When_NoBudget() {
        // Arrange - връщаме празен Optional (няма budget)
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.empty());

        // Act
        budgetCalculationService.calculateBudget("test@gmail.com", 1L, "2025-08",
                BigDecimal.valueOf(500));

        // Assert - нищо не трябва да се случи
        verify(notificationRepository, never()).save(any());
        verify(budgetRepository, never()).save(any());
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

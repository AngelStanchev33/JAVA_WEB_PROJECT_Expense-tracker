package com.example.expense_tracker.service;

import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.NotificationEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.entity.CurrencyEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
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

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExRateService exRateService;

    private BudgetCalculationService budgetCalculationService;


    @BeforeEach
    void setUp() {
        budgetCalculationService = new BudgetCalculationServiceImpl(budgetRepository,
                expenseRepository, notificationRepository, exRateService);
    }

    @Test
    void calculateBudget_Should_Send_Notification() {
        ExpenseCreatedEvent event = new ExpenseCreatedEvent(1L, "test@gmail.com",
                BigDecimal.valueOf(500), "BGN", "2025-08");
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));

        when(budgetRepository.findByUserEmailAndAndMonth(event.userEmail(),
                event.month())).thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(500)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated(event.userEmail(), event.expenseId(),
                event.month());

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        NotificationEntity savedNotification = captor.getValue();
        assertEquals("Warning! Budget running low", savedNotification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_WARNING_50, savedNotification.getType());
        assertEquals(owner.getId(), savedNotification.getUser().getId());

    }

    @Test
    void calculateBudget_Should_DoNothing_When_NoBudget() {
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.empty());

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        verify(notificationRepository, never()).save(any());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void calculateBudgetWhenBudgetIsCreated_Should_ThrowException_When_BudgetNotFound() {
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.example.expense_tracker.exception.BudgetNotFoundException.class, 
            () -> budgetCalculationService.calculateBudgetWhenBudgetIsCreated(999L));
    }

    @Test
    void calculateBudgetWhenBudgetIsCreated_Should_RecalculateSpent_When_BudgetExists() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        budgetCalculationService.calculateBudgetWhenBudgetIsCreated(1L);

        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void should_Generate_25Percent_Warning_Notification() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(750)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        
        NotificationEntity notification = captor.getValue();
        assertEquals("Critical warning! Budget almost depleted", notification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_WARNING_25, notification.getType());
    }

    @Test
    void should_Generate_75Percent_Warning_Notification() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(750)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        
        NotificationEntity notification = captor.getValue();
        assertEquals("Critical warning! Budget almost depleted", notification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_WARNING_25, notification.getType());
    }

    @Test
    void should_Generate_Budget_Exceeded_Notification() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(1200)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        
        NotificationEntity notification = captor.getValue();
        assertEquals("You've officially overspent", notification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_EXCEEDED, notification.getType());
    }

    @Test
    void should_Not_Generate_Notification_When_Above_75Percent() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(200)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        verify(notificationRepository, never()).save(any());
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void should_Convert_Currency_When_Expense_And_Budget_Different_Currencies() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        ExpenseEntity usdExpense = new ExpenseEntity();
        usdExpense.setId(1L);
        usdExpense.setAmount(BigDecimal.valueOf(100));
        usdExpense.setCurrency(createCurrency("USD"));
        usdExpense.setUser(owner);
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(List.of(usdExpense));
        when(exRateService.convert("USD", "BGN", BigDecimal.valueOf(100)))
                .thenReturn(BigDecimal.valueOf(180));
        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        verify(exRateService, times(1)).convert("USD", "BGN", BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(180), budget.getSpent());
    }

    @Test
    void should_Not_Convert_When_Same_Currency() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(500)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        verify(exRateService, never()).convert(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void should_Calculate_Total_From_Multiple_Expenses_In_Month() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), owner, "2025-08", BigDecimal.valueOf(0));
        
        List<ExpenseEntity> multipleExpenses = List.of(
                createExpense(BigDecimal.valueOf(300), "BGN"),
                createExpense(BigDecimal.valueOf(250), "BGN"),
                createExpense(BigDecimal.valueOf(250), "BGN")
        );
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(multipleExpenses);

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        assertEquals(BigDecimal.valueOf(800), budget.getSpent());
        
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        
        NotificationEntity notification = captor.getValue();
        assertEquals("Critical warning! Budget almost depleted", notification.getMessage());
    }

    @Test
    void should_Handle_Zero_Budget_Limit_And_Generate_Exceeded_Notification() {
        UserEntity owner = createUser();
        BudgetEntity budget = createBudget(BigDecimal.ZERO, owner, "2025-08", BigDecimal.valueOf(0));
        
        when(budgetRepository.findByUserEmailAndAndMonth("test@gmail.com", "2025-08"))
                .thenReturn(Optional.of(budget));
        when(expenseRepository.findAllByUserEmailAndYearAndMonth("test@gmail.com", 2025, 8))
                .thenReturn(createExpenseList(BigDecimal.valueOf(100)));

        budgetCalculationService.calculateBudgetWhenExpenseIsCreated("test@gmail.com", 1L, "2025-08");

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        
        NotificationEntity notification = captor.getValue();
        assertEquals("You've officially overspent", notification.getMessage());
        assertEquals(NotificationTypeEnum.BUDGET_EXCEEDED, notification.getType());
    }

    private ExpenseEntity createExpense(BigDecimal amount, String currencyCode) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setAmount(amount);
        expense.setCurrency(createCurrency(currencyCode));
        expense.setUser(createUser());
        
        return expense;
    }

    private BudgetEntity createBudget(BigDecimal limit, UserEntity owner, String month, BigDecimal spent) {
        BudgetEntity entity = new BudgetEntity();
        entity.setId(1L);
        entity.setBudgetLimit(limit);
        entity.setUser(owner);
        entity.setMonth(month);
        entity.setSpent(spent);
        entity.setCurrency(createCurrency("BGN"));

        return entity;
    }

    private UserEntity createUser() {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName(UserRoleEnum.USER);

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstname("Test");
        entity.setLastname("Testov");
        entity.setEmail("test@gmail.com");
        entity.setRoles(List.of(userRole));

        return entity;
    }

    private List<ExpenseEntity> createExpenseList(BigDecimal totalAmount) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setAmount(totalAmount);
        expense.setCurrency(createCurrency("BGN"));
        expense.setUser(createUser());
        
        return List.of(expense);
    }

    private CurrencyEntity createCurrency(String code) {
        CurrencyEntity currency = new CurrencyEntity();
        currency.setId(1L);
        currency.setCode(code);
        
        return currency;
    }


}

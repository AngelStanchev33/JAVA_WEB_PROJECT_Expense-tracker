package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.BudgetNotFoundException;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.entity.NotificationEntity;
import com.example.expense_tracker.model.enums.NotificationTypeEnum;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.NotificationRepository;
import com.example.expense_tracker.service.BudgetCalculationService;
import com.example.expense_tracker.service.ExRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class BudgetCalculationServiceImpl implements BudgetCalculationService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final NotificationRepository notificationRepository;
    private final ExRateService exRateService;

    public BudgetCalculationServiceImpl(BudgetRepository budgetRepository, ExpenseRepository expenseRepository, NotificationRepository notificationRepository, ExRateService exRateService) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.notificationRepository = notificationRepository;
        this.exRateService = exRateService;
    }

    @Override
    public void calculateBudgetWhenExpenseIsCreated(String userEmail, Long expenseId, String month) {
        Optional<BudgetEntity> optionalEntity = budgetRepository.findByUserEmailAndAndMonth(userEmail, month);

        if (optionalEntity.isEmpty()) {
            // Няма budget за този месец - skip логиката
            return;
        }

        BudgetEntity budget = optionalEntity.get();
        updateBudgetSpentAndNotifications(budget, userEmail, expenseId);
    }

    @Override
    public void calculateBudgetWhenBudgetIsCreated(Long id) {
        BudgetEntity budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));

        updateBudgetSpentAndNotifications(budget, budget.getUser().getEmail(), null);
    }

    private void updateBudgetSpentAndNotifications(BudgetEntity budget, String userEmail, Long expenseId) {
        // Parse month to get year and month integers
        YearMonth yearMonth = YearMonth.parse(budget.getMonth());
        int yearOfExpense = yearMonth.getYear();
        int monthOfExpense = yearMonth.getMonthValue();

        // Recalculate total spent from ALL expenses for the month
        BigDecimal totalSpent = expenseRepository
                .findAllByUserEmailAndYearAndMonth(userEmail, yearOfExpense, monthOfExpense)
                .stream()
                .map(expense -> convertExpenseAmountToBudgetCurrency(expense, budget))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Update budget with recalculated spent amount
        budget.setSpent(totalSpent);
        budgetRepository.save(budget);

        // Generate notifications if budget thresholds are reached
        BigDecimal percentageLeft = calculateRemainingPercentage(totalSpent, budget.getBudgetLimit());
        NotificationEntity notification = generateNotification(percentageLeft);

        if (notification != null) {
            notification.setUser(budget.getUser());
            notification.setRelatedBudgetId(budget.getId());
            if (expenseId != null) {
                notification.setRelatedExpenseId(expenseId);
            }
            notificationRepository.save(notification);
        }
    }

    private BigDecimal convertExpenseAmountToBudgetCurrency(ExpenseEntity expense, BudgetEntity budget) {
        if (expense.getCurrency().getCode().equals(budget.getCurrency().getCode())) {
            return expense.getAmount();
        }

        return exRateService.convert(
                expense.getCurrency().getCode(),
                budget.getCurrency().getCode(),
                expense.getAmount()
        );
    }

    private BigDecimal calculateRemainingPercentage(BigDecimal spentAmount, BigDecimal budgetLimit) {
        BigDecimal remaining = budgetLimit.subtract(spentAmount);
        return remaining
                .divide(budgetLimit, 2, RoundingMode.HALF_UP)     // процент
                .multiply(new BigDecimal("100"));
    }

    private NotificationEntity generateNotification(BigDecimal percentageLeft) {
        double percent = percentageLeft.doubleValue();
        NotificationEntity entity = new NotificationEntity();

        if (percent <= 0) {
            entity.setType(NotificationTypeEnum.BUDGET_EXCEEDED);
            entity.setMessage("💀 RIP Budget! You've officially overspent");
        } else if (percent <= 25) {
            entity.setType(NotificationTypeEnum.BUDGET_WARNING_25);
            entity.setMessage("⚠️ Danger zone! Only 25% budget left");
        } else if (percent <= 50) {
            entity.setType(NotificationTypeEnum.BUDGET_WARNING_50);
            entity.setMessage("💸 Halfway to broke! 50% budget gone");
        } else if (percent <= 75) {
            entity.setType(NotificationTypeEnum.BUDGET_WARNING_75);
            entity.setMessage("🔥 Wallet is getting lighter! 75% spent");
        } else {
            return null;
        }
        return entity;
    }


}

package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.BudgetNotFoundException;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.NotificationEntity;
import com.example.expense_tracker.model.enums.NotificationTypeEnum;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.NotificationRepository;
import com.example.expense_tracker.service.BudgetCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BudgetCalculationServiceImpl implements BudgetCalculationService {

    private final BudgetRepository budgetRepository;
    private final NotificationRepository notificationRepository;

    public BudgetCalculationServiceImpl(BudgetRepository budgetRepository, NotificationRepository notificationRepository) {
        this.budgetRepository = budgetRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void calculateBudget(String userEmail, Long expenseId, String month, BigDecimal amount) {
        BudgetEntity budgetEntity = budgetRepository.findByUserEmailAndAndMonth(userEmail, month).orElseThrow(
                BudgetNotFoundException::new);

        BigDecimal spentAmount = budgetEntity.getSpent().add(amount);
        BigDecimal percentageLeft = calculateRemainingPercentage(spentAmount, budgetEntity.getBudgetLimit());


        NotificationEntity alarm = generateNotification(percentageLeft);
        
        if (alarm != null) {
            alarm.setUser(budgetEntity.getUser());
            alarm.setRelatedBudgetId(budgetEntity.getId());
            alarm.setRelatedExpenseId(expenseId);
            notificationRepository.save(alarm);
        }

        budgetEntity.setSpent(spentAmount);
        budgetRepository.save(budgetEntity);

    }

    private BigDecimal calculateRemainingPercentage(BigDecimal spentAmount, BigDecimal budgetLimit) {
        BigDecimal remaining = budgetLimit.subtract(spentAmount);
        return remaining
                .divide(budgetLimit, 4, RoundingMode.HALF_UP)     // процент
                .multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP);
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

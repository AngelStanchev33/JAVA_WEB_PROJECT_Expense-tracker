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
import java.util.Optional;

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
        Optional<BudgetEntity> optionalEntity = budgetRepository.findByUserEmailAndAndMonth(userEmail, month);

        if (optionalEntity.isEmpty()) {
            // Няма budget за този месец - skip логиката
            return;
        }

        BudgetEntity entity = optionalEntity.get();

        BigDecimal spentAmount = entity.getSpent().add(amount);
        BigDecimal percentageLeft = calculateRemainingPercentage(spentAmount, entity.getBudgetLimit());

        NotificationEntity alarm = generateNotification(percentageLeft);

        if (alarm != null) {
            alarm.setUser(entity.getUser());
            alarm.setRelatedBudgetId(entity.getId());
            alarm.setRelatedExpenseId(expenseId);
            notificationRepository.save(alarm);
        }

        entity.setSpent(spentAmount);
        budgetRepository.save(entity);

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

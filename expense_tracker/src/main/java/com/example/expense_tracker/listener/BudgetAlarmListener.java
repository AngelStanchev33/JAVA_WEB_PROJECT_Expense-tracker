package com.example.expense_tracker.listener;

import com.example.expense_tracker.model.event.BudgetCreatedEvent;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.service.BudgetCalculationService;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BudgetAlarmListener {

    private final BudgetCalculationService budgetCalculationService;


    public BudgetAlarmListener(BudgetCalculationService budgetCalculationService) {
        this.budgetCalculationService = budgetCalculationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void calculateBudget(ExpenseCreatedEvent event) {
        budgetCalculationService.calculateBudgetWhenExpenseIsCreated(
                event.userEmail(),
                event.expenseId(),
                event.month());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void calculateBudget(BudgetCreatedEvent event) {
        budgetCalculationService.calculateBudgetWhenBudgetIsCreated(event.budgetID());
    }


}

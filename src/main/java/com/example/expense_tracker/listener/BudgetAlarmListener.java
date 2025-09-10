package com.example.expense_tracker.listener;

import com.example.expense_tracker.model.event.BudgetCreatedEvent;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.service.BudgetCalculationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BudgetAlarmListener {

    private final BudgetCalculationService budgetCalculationService;


    public BudgetAlarmListener(BudgetCalculationService budgetCalculationService) {
        this.budgetCalculationService = budgetCalculationService;
    }

    @EventListener
    public void calculateBudget(ExpenseCreatedEvent event) {
        budgetCalculationService.calculateBudgetWhenExpenseIsCreated(
                event.userEmail(),
                event.expenseId(),
                event.month());
    }

    @EventListener
    public void calculateBudget(BudgetCreatedEvent event) {
        budgetCalculationService.calculateBudgetWhenBudgetIsCreated(event.budgetID());
    }


}

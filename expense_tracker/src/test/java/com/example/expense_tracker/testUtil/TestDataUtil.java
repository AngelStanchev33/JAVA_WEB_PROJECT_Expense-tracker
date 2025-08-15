package com.example.expense_tracker.testUtil;

import com.example.expense_tracker.exception.CategoryNotFoundException;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.CategoryEntity;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.CategoryRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TestDataUtil {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    public ExpenseEntity createExpense(UserEntity ownerId, CategoryEnum category) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setUser(ownerId);
        expense.setExpenseDate(LocalDate.now());
        expense.setCategory(categoryRepository.findByCategoryEnum(category)
                .orElseThrow(() -> new CategoryNotFoundException(category + "not found")));
        expense.setDescription("test");
        expense.setAmount(BigDecimal.valueOf(2.50));

        expenseRepository.save(expense);

        return expense;
    }

    public BudgetEntity createBudget(UserEntity ownerId,
                                     String month,
                                     BigDecimal spent,
                                     BigDecimal limit) {
        BudgetEntity budget = new BudgetEntity();
        budget.setMonth(month);
        budget.setSpent(spent);
        budget.setBudgetLimit(limit);
        budget.setUser(ownerId);

        budgetRepository.save(budget);

        return budget;
    }

    public void cleanUp() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
    }
}

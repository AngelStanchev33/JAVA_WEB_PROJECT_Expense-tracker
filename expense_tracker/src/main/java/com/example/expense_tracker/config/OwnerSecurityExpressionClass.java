package com.example.expense_tracker.config;

import com.example.expense_tracker.exception.NotOwnerException;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.ExpenseService;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
public class OwnerSecurityExpressionClass extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private ExpenseService expenseService;
    private BudgetService budgetService;
    private Object filterObject;
    private Object returnObject;

    public OwnerSecurityExpressionClass(Authentication authentication) {
        super(authentication);
    }

    public boolean isExpenseOwner(Long id) {
        String username = currentUserName();

        if (username == null) {
            throw new NotOwnerException("User not authenticated");
        }

        if (id == null) {
            throw new NotOwnerException("Expense ID is null");
        }

        boolean isOwnerExpense = expenseService.isOwner(id, username);
        if (!isOwnerExpense) {
            throw new NotOwnerException("Access denied");
        }
        
        return true; // Always true if no exception thrown
    }

    public boolean isBudgetOwner(Long id) {
        String username = currentUserName();

        if (username == null) {
            throw new NotOwnerException("User not authenticated");
        }

        if (id == null) {
            throw new NotOwnerException("Expense ID is null");
        }

        boolean isOwnerExpense = budgetService.isOwner(id, username);
        if (!isOwnerExpense) {
            throw new NotOwnerException("Access denied");
        }

        return true; // Always true if no exception thrown
    }

    public String currentUserName() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails user) {
            return user.getUsername();
        }

        return null;
    }


    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

}


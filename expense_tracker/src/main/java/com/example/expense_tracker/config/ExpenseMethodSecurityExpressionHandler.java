package com.example.expense_tracker.config;

import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.ExpenseService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class ExpenseMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    public ExpenseMethodSecurityExpressionHandler(ExpenseService expenseService, BudgetService budgetService) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        OwnerSecurityExpressionClass root = new OwnerSecurityExpressionClass(authentication);
        root.setExpenseService(expenseService);
        root.setBudgetService(budgetService);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }
}

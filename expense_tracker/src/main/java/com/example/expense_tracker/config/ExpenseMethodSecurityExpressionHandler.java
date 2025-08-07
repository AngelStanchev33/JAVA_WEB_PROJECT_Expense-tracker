package com.example.expense_tracker.config;

import com.example.expense_tracker.service.ExpenseService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class ExpenseMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final ExpenseService expenseService;

    public ExpenseMethodSecurityExpressionHandler(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        OwnerSecurityExpressionClass root = new OwnerSecurityExpressionClass(authentication);
        root.setExpenseService(expenseService);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }
}

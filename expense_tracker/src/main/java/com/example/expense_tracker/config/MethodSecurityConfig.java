package com.example.expense_tracker.config;


import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SuppressWarnings("deprecation")
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private BudgetService budgetService;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new ExpenseMethodSecurityExpressionHandler(expenseService, budgetService);
    }
}

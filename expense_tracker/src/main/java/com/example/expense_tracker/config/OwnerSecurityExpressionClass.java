package com.example.expense_tracker.config;

import com.example.expense_tracker.service.ExpenseService;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
public class OwnerSecurityExpressionClass extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private ExpenseService expenseService;
    private Object filterObject;
    private Object returnObject;

    public OwnerSecurityExpressionClass(Authentication authentication) {
        super(authentication);
    }

    public boolean isOwner(Long id) {
        String username = currentUserName();

        if (username == null) {
            return false;
        }

        if (id == null) {
            return false;
        }

        return expenseService.isOwner(id, username);
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


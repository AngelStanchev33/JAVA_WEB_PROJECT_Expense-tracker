package com.example.expense_tracker.model.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class ExpenseTrackerUserDetails extends User {

    private Long id;
    private String firstname;
    private String lastname;

    public ExpenseTrackerUserDetails(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String firstname,
            String lastname) {

        super(username, password, authorities);
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}

package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
public class UserPreferenceEntity extends BaseEntity {

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "default_currency_code", nullable = true)
    private CurrencyEntity defaultCurrency;

}

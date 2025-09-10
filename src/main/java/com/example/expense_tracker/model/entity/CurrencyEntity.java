package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
public class CurrencyEntity extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 3, name = "code")
    private String code;

}

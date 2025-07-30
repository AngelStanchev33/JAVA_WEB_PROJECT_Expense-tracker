package com.example.expense_tracker.model.entity;

import com.example.expense_tracker.model.enums.CategoryEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity extends BaseEntity {

    @NotNull
    @Column(name = "category", nullable = false, unique = true, length = 50)
    @Enumerated(value = EnumType.STRING)
    private CategoryEnum categoryEnum;
}

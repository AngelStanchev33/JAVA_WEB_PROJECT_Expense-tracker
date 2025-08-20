package com.example.expense_tracker.model.entity;

import com.example.expense_tracker.model.enums.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class UserRoleEntity extends BaseEntity {

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private UserRoleEnum roleName;

}

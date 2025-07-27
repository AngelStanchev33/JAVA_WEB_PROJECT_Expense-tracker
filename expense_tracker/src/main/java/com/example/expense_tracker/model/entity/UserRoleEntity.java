package com.example.expense_tracker.model.entity;

import com.example.expense_tracker.model.entity.enums.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_roles")
public class UserRoleEntity extends BaseEntity {

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private UserRoleEnum roleName;

    public UserRoleEnum getRoleName() { return roleName; }

    public UserRoleEntity setRoleName(UserRoleEnum roleName) {
        this.roleName = roleName;
        return this;
    }
}

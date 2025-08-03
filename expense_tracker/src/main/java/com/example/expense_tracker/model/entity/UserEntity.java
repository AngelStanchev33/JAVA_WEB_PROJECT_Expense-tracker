package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, name = "email", length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false, name = "first_name", length = 255)
    private String firstname;

    @NotBlank
    @Column(nullable = false, name = "password", length = 255)
    private String password;

    @NotBlank
    @Column(nullable = false, name = "last_name", length = 255)
    private String lastname;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull
    @Column(nullable = false, name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive = true;

    @ManyToMany
    @JoinTable(name = "users_roles_mapping", 
               joinColumns = @JoinColumn(name = "user_id"), 
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<UserRoleEntity> roles;

    @OneToMany(mappedBy = "user")
    private List<ExpenseEntity> expenses;

}

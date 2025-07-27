package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;


import java.util.List;

@Entity
@Table(name = "users")
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

    public String getEmail() { return email; }
    public String getFirstname() { return firstname; }
    public String getPassword() { return password; }
    public String getLastname() { return lastname; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return isActive; }
    public List<UserRoleEntity> getRoles() { return roles; }

    public void setEmail(String email) { this.email = email; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setPassword(String password) { this.password = password; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setActive(boolean active) { isActive = active; }
    public void setRoles(List<UserRoleEntity> roles) { this.roles = roles; }
}

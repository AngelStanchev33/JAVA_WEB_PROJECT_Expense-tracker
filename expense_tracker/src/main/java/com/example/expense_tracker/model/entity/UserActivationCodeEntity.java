package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_activation_codes")
@Getter
@Setter
@Accessors(chain = true)
public class UserActivationCodeEntity extends BaseEntity {

    @Column(name = "activation_code")
    private String activationCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
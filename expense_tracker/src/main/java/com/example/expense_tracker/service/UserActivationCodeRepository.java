package com.example.expense_tracker.service;

import com.example.expense_tracker.model.entity.UserActivationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivationCodeRepository extends JpaRepository<UserActivationCodeEntity, Long> {
}

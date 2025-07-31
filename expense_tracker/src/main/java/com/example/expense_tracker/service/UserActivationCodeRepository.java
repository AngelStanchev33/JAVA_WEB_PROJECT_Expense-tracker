package com.example.expense_tracker.service;

import com.example.expense_tracker.model.entity.UserActivationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserActivationCodeRepository extends JpaRepository<UserActivationCodeEntity, Long> {

    @Modifying
    @Query("DELETE FROM UserActivationCodeEntity u WHERE u.createdAt < :expiredBefore")
    void deleteExpiredActivationCodes(LocalDateTime expiredBefore);

}

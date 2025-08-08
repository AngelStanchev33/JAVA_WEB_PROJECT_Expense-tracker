package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findAllByUserEmail(String userEmail);

    Optional<ExpenseEntity> findByIdAndUserEmail(long id, String username);
}
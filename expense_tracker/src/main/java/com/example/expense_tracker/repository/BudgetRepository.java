package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {

    List<BudgetEntity> findByUserEmail(String email);

    Optional<BudgetEntity> findByUserEmailAndAndMonth(String email, String month);

    Optional<BudgetEntity> findByIdAndUserEmail(Long budgetId, String email);
}
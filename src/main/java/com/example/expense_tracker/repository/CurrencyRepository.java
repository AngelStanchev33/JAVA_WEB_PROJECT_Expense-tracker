package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    
    Optional<CurrencyEntity> findByCode(String code);
}
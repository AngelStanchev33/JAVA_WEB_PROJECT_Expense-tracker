package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findAllByUserEmail(String userEmail);

    Optional<ExpenseEntity> findByIdAndUserEmail(long id, String username);

    @Query("select e from ExpenseEntity e where e.user.email =:email and year(e.createdAt) = :year and " +
            "month(e.createdAt) = :month ")
    List<ExpenseEntity> findAllByUserEmailAndYearAndMonth(@Param("email") String email,
                                                          @Param("year") int year,
                                                          @Param("month") int month);
}
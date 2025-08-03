package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.CategoryEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByCategoryEnum(CategoryEnum categoryEnum);
    

}
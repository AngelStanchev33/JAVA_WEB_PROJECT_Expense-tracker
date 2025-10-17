package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity,Long > {

    List<NotificationEntity> findAllByUserEmail(String email);
}

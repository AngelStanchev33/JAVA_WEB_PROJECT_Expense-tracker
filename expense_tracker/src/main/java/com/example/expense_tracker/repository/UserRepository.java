package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByEmail(String email);

    @Query("select u from UserEntity u join fetch u.roles where u.email = :email")
    Optional<UserEntity> findByEmailWithUserRoles(@Param("email") String email);
}
package com.airesume.resumescreeningtool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airesume.resumescreeningtool.entity.User;
import com.airesume.resumescreeningtool.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    List<User> findByIsActiveTrue();
    List<User> findByIsActiveFalse();
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
}

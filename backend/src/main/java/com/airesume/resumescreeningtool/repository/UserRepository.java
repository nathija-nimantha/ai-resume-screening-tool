package com.airesume.resumescreeningtool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airesume.resumescreeningtool.entity.User;
import com.airesume.resumescreeningtool.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find user by username or email (useful for login)
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Find all active users
    List<User> findByIsActiveTrue();
    
    // Find all inactive users
    List<User> findByIsActiveFalse();
    
    // Find users by role
    List<User> findByRole(UserRole role);
    
    // Find active users by role
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    
    // Find users by company name
    List<User> findByCompanyName(String companyName);
    
    // Find active users by company name
    List<User> findByCompanyNameAndIsActiveTrue(String companyName);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users by first name and last name
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Custom query to find users with job postings count
    @Query("SELECT u FROM User u LEFT JOIN u.jobPostings jp WHERE u.isActive = true GROUP BY u HAVING COUNT(jp) > :count")
    List<User> findActiveUsersWithJobPostingsGreaterThan(@Param("count") long count);
    
    // Find users by role with company name containing keyword
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.companyName LIKE %:keyword% AND u.isActive = true")
    List<User> findByRoleAndCompanyNameContaining(@Param("role") UserRole role, @Param("keyword") String keyword);
}

package com.airesume.resumescreeningtool.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.airesume.resumescreeningtool.entity.User;
import com.airesume.resumescreeningtool.entity.UserRole;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        // Given
        User user = new User("testuser", "test@example.com", "password", UserRole.HR_MANAGER);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCompanyName("Test Company");
        
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getRole()).isEqualTo(UserRole.HR_MANAGER);
    }

    @Test
    public void testFindByEmail() {
        // Given
        User user = new User("testuser2", "test2@example.com", "password", UserRole.RECRUITER);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("test2@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser2");
        assertThat(found.get().getRole()).isEqualTo(UserRole.RECRUITER);
    }

    @Test
    public void testExistsByUsername() {
        // Given
        User user = new User("existinguser", "existing@example.com", "password", UserRole.ADMIN);
        entityManager.persistAndFlush(user);

        // When & Then
        assertThat(userRepository.existsByUsername("existinguser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistentuser")).isFalse();
    }

    @Test
    public void testFindByRole() {
        // Given
        User hrManager = new User("hr1", "hr1@example.com", "password", UserRole.HR_MANAGER);
        User recruiter = new User("recruiter1", "recruiter1@example.com", "password", UserRole.RECRUITER);
        
        entityManager.persistAndFlush(hrManager);
        entityManager.persistAndFlush(recruiter);

        // When
        var hrManagers = userRepository.findByRole(UserRole.HR_MANAGER);
        var recruiters = userRepository.findByRole(UserRole.RECRUITER);

        // Then
        assertThat(hrManagers).hasSize(1);
        assertThat(hrManagers.get(0).getUsername()).isEqualTo("hr1");
        
        assertThat(recruiters).hasSize(1);
        assertThat(recruiters.get(0).getUsername()).isEqualTo("recruiter1");
    }

    @Test
    public void testFindByIsActiveTrue() {
        // Given
        User activeUser = new User("active", "active@example.com", "password", UserRole.HR_MANAGER);
        activeUser.setIsActive(true);
        
        User inactiveUser = new User("inactive", "inactive@example.com", "password", UserRole.HR_MANAGER);
        inactiveUser.setIsActive(false);
        
        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        // When
        var activeUsers = userRepository.findByIsActiveTrue();

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("active");
    }
}

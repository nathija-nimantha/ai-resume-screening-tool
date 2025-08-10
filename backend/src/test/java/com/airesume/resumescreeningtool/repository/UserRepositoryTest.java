package com.airesume.resumescreeningtool.repository;

import com.airesume.resumescreeningtool.entity.User;
import com.airesume.resumescreeningtool.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByUsername() {
        User user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .role(UserRole.USER)
                .isActive(true)
                .firstName("Test")
                .lastName("User")
                .build();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void testFindByEmail() {
        User user = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .password("password")
                .role(UserRole.RECRUITER)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("test2@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser2");
        assertThat(found.get().getRole()).isEqualTo(UserRole.RECRUITER);
    }

    @Test
    public void testExistsByUsername() {
        User user = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("password")
                .role(UserRole.ADMIN)
                .build();

        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByUsername("existinguser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistentuser")).isFalse();
    }

    @Test
    public void testFindByRole() {
        User hrManager = User.builder()
                .username("hr1")
                .email("hr1@example.com")
                .password("password")
                .role(UserRole.HR_MANAGER)
                .build();

        User recruiter = User.builder()
                .username("recruiter1")
                .email("recruiter1@example.com")
                .password("password")
                .role(UserRole.RECRUITER)
                .build();

        entityManager.persistAndFlush(hrManager);
        entityManager.persistAndFlush(recruiter);

        var hrManagers = userRepository.findByRole(UserRole.HR_MANAGER);
        var recruiters = userRepository.findByRole(UserRole.RECRUITER);

        assertThat(hrManagers).hasSize(1);
        assertThat(hrManagers.get(0).getUsername()).isEqualTo("hr1");

        assertThat(recruiters).hasSize(1);
        assertThat(recruiters.get(0).getUsername()).isEqualTo("recruiter1");
    }

    @Test
    public void testFindByIsActiveTrue() {
        User activeUser = User.builder()
                .username("active")
                .email("active@example.com")
                .password("password")
                .role(UserRole.HR_MANAGER)
                .isActive(true)
                .build();

        User inactiveUser = User.builder()
                .username("inactive")
                .email("inactive@example.com")
                .password("password")
                .role(UserRole.HR_MANAGER)
                .isActive(false)
                .build();

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        var activeUsers = userRepository.findByIsActiveTrue();

        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("active");
    }
}

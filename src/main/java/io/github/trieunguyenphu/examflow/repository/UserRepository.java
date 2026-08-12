package io.github.trieunguyenphu.examflow.repository;

import io.github.trieunguyenphu.examflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findByRoleOrderByFullName(String role);
    long countByRole(String role);
}

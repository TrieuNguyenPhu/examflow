package io.github.trieunguyenphu.examflow.service;

import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerStudent(User user) {
        user.setUsername(normalizeUsername(user.getUsername()));
        user.setFullName(user.getFullName().trim());
        user.setMobileNumber(user.getMobileNumber() == null ? null : user.getMobileNumber().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.ROLE_STUDENT);
        users.save(user);
    }

    public User findByUsername(String username) {
        return users.findByUsernameIgnoreCase(normalizeUsername(username)).orElse(null);
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}

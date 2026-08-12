package io.github.trieunguyenphu.examflow;

import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ExamFlowApplication {

    private static final Logger log = LoggerFactory.getLogger(ExamFlowApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ExamFlowApplication.class, args);
    }

    @Bean
    CommandLineRunner bootstrapAdmin(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:Administrator}") String name) {
        return args -> {
            if (username.isBlank() && password.isBlank()) {
                log.info("Admin bootstrap skipped. Set APP_ADMIN_USERNAME and APP_ADMIN_PASSWORD to create one.");
                return;
            }
            if (username.isBlank() || password.length() < 12) {
                throw new IllegalStateException("Admin bootstrap requires a username and a password of at least 12 characters.");
            }

            String normalizedUsername = username.trim().toLowerCase();
            if (users.findByUsernameIgnoreCase(normalizedUsername).isPresent()) {
                log.info("Admin account already exists: {}", normalizedUsername);
                return;
            }

            User admin = new User();
            admin.setUsername(normalizedUsername);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(User.ROLE_ADMIN);
            admin.setFullName(name.trim());
            users.save(admin);
            log.info("Admin account created: {}", normalizedUsername);
        };
    }
}

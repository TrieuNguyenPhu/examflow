package io.github.trieunguyenphu.examflow.config;

import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository users;

    public GlobalControllerAdvice(UserRepository users) {
        this.users = users;
    }

    @ModelAttribute("currentUser")
    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) return null;
        return users.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
    }

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

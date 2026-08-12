package io.github.trieunguyenphu.examflow.controller;

import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.service.FileStorageService;
import io.github.trieunguyenphu.examflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {

    private final UserService userService;
    private final FileStorageService files;

    public AuthController(UserService userService, FileStorageService files) {
        this.userService = userService;
        this.files = files;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return "index";
        boolean admin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(User.ROLE_ADMIN::equals);
        return admin ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "profilePicFile", required = false) MultipartFile profilePicFile,
            Model model) {
        if (userService.findByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "duplicate", "An account already exists with that email.");
        }
        if (bindingResult.hasErrors()) return "register";

        if (profilePicFile != null && !profilePicFile.isEmpty()) {
            try {
                user.setProfilePicUrl(files.saveProfileImage(profilePicFile));
            } catch (IllegalArgumentException | IllegalStateException exception) {
                model.addAttribute("fileError", exception.getMessage());
                return "register";
            }
        }

        userService.registerStudent(user);
        return "redirect:/login?registered=true";
    }
}

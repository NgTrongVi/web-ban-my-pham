package com.comestics.cosmetics_store.controller;

import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        User user = User.builder()
                .email(form.getEmail())
                .fullName(form.getFullName())
                .address(form.getAddress())
                .phone(form.getPhone())
                .build();
        try {
            userService.registerCustomer(user, form.getPassword());
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
        return "redirect:/login";
    }

    @Data
    public static class RegisterForm {
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        private String email;

        @jakarta.validation.constraints.NotBlank
        private String password;

        @jakarta.validation.constraints.NotBlank
        private String fullName;

        private String phone;
        private String address;
    }
}

package com.comestics.cosmetics_store.controller;

import com.comestics.cosmetics_store.entity.Role;
import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PostLoginController {

    private final UserService userService;

    @GetMapping("/post-login")
    public String postLogin() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean isAdmin = currentUser.getRoles().stream()
                .map(Role::getName)
                .anyMatch(r -> r.equals("ROLE_ADMIN"));

        if (isAdmin) {
            // admin → vào dashboard
            return "redirect:/admin";
        }

        // user thường → về trang home
        return "redirect:/";
    }
}

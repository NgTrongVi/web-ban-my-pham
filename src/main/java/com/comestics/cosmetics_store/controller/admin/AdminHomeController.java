package com.comestics.cosmetics_store.controller.admin;

import com.comestics.cosmetics_store.repository.OrderRepository;
import com.comestics.cosmetics_store.repository.ProductRepository;
import com.comestics.cosmetics_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHomeController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("totalRevenue", orderRepository.sumTotalAmount());
        return "admin/dashboard";
    }
}

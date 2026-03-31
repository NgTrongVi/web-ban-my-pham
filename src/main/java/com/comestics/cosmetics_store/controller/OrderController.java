package com.comestics.cosmetics_store.controller;

import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.service.OrderService;
import com.comestics.cosmetics_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String orderList(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String skinType,
                            @RequestParam(required = false) BigDecimal minPrice,
                            @RequestParam(required = false) BigDecimal maxPrice,
                            Model model) {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        // dùng hàm searchOrdersOfUser để áp dụng tìm kiếm + lọc
        model.addAttribute("orders",
                orderService.searchOrdersOfUser(currentUser,
                        keyword, skinType, minPrice, maxPrice));

        // để Thymeleaf bind lại vào form filter
        model.addAttribute("keyword", keyword);
        model.addAttribute("skinType", skinType);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "user/orders";
    }
}

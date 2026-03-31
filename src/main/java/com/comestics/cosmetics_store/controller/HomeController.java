package com.comestics.cosmetics_store.controller;

import com.comestics.cosmetics_store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(@RequestParam(value = "keyword", required = false) String keyword,
                       Model model) {

        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("products", productService.search(keyword));
        } else {
            model.addAttribute("products", productService.getAll());
        }

        // để giữ lại giá trị ô tìm kiếm
        model.addAttribute("keyword", keyword);

        return "user/home";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "user/product-detail";
    }
}

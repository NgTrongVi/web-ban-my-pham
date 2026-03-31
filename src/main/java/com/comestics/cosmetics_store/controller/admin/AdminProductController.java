package com.comestics.cosmetics_store.controller.admin;

import com.comestics.cosmetics_store.entity.Brand;
import com.comestics.cosmetics_store.entity.Category;
import com.comestics.cosmetics_store.entity.Product;
import com.comestics.cosmetics_store.repository.BrandRepository;
import com.comestics.cosmetics_store.repository.CategoryRepository;
import com.comestics.cosmetics_store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product formProduct) {

        if (formProduct.getId() == null) {
            // THÊM MỚI: chưa có id -> lưu trực tiếp
            productRepository.save(formProduct);
        } else {
            // CHỈNH SỬA: lấy product cũ, cập nhật field đơn giản, KHÔNG đụng tới skus
            Product product = productRepository.findById(formProduct.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.setName(formProduct.getName());
            product.setDescription(formProduct.getDescription());
            product.setBrand(formProduct.getBrand());
            product.setCategory(formProduct.getCategory());
            product.setSkinType(formProduct.getSkinType());
            product.setBasePrice(formProduct.getBasePrice());
            product.setThumbnailUrl(formProduct.getThumbnailUrl());

            productRepository.save(product);
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productRepository.findById(id).orElseThrow());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}

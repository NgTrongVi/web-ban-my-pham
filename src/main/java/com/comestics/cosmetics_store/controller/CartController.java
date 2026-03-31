package com.comestics.cosmetics_store.controller;

import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.service.CartService;
import com.comestics.cosmetics_store.service.OrderService;
import com.comestics.cosmetics_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    // Hiển thị giỏ hàng
    @GetMapping
    public String viewCart(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("items", cartService.getCartItems(currentUser));
        return "user/cart";
    }

    // Thêm vào giỏ
    @PostMapping("/add")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        User currentUser = userService.getCurrentUser();
        cartService.addToCart(currentUser, skuId, quantity);
        return "redirect:/cart";
    }
 // Thêm vào giỏ đối với sản phẩm chưa có SKU - tạo SKU mặc định
    @PostMapping("/add-default")
    public String addDefaultToCart(@RequestParam("productId") Long productId,
                                   @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        User currentUser = userService.getCurrentUser();
        cartService.addDefaultSkuToCart(currentUser, productId, quantity);
        return "redirect:/cart";
    }


    // Xóa / Thanh toán các sản phẩm đã chọn
    @PostMapping("/update")
    public String updateCart(@RequestParam(value = "selectedIds", required = false) List<Long> selectedIds,
                             @RequestParam("action") String action) {
        User currentUser = userService.getCurrentUser();

        if (selectedIds == null || selectedIds.isEmpty()) {
            return "redirect:/cart";
        }

        if ("delete".equals(action)) {
            // Xóa các item đã tích
            for (Long id : selectedIds) {
                cartService.removeFromCart(currentUser, id);
            }
            return "redirect:/cart";
        } else if ("checkout".equals(action)) {
            // Thanh toán các item đã tích
            orderService.placeOrder(currentUser, selectedIds);
            return "redirect:/orders";
        }

        return "redirect:/cart";
    }
}

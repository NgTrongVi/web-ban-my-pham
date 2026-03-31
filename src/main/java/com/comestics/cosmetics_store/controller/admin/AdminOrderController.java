package com.comestics.cosmetics_store.controller.admin;

import com.comestics.cosmetics_store.entity.Order;
import com.comestics.cosmetics_store.entity.OrderStatus;
import com.comestics.cosmetics_store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.comestics.cosmetics_store.entity.OrderStatus;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       Model model) {
        model.addAttribute("orders", orderService.findAll(status));
        model.addAttribute("status", status);
        model.addAttribute("allStatuses", OrderStatus.values());
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        model.addAttribute("allStatuses", OrderStatus.values());
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders/{id}";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.CONFIRMED); // chỉnh tên enum theo project của bạn
        return "redirect:/admin/orders/{id}";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.CANCELED); // chỉnh tên enum nếu khác
        return "redirect:/admin/orders/{id}";
    }
}

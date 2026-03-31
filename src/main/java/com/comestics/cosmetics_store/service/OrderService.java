package com.comestics.cosmetics_store.service;

import com.comestics.cosmetics_store.entity.Order;
import com.comestics.cosmetics_store.entity.OrderStatus;
import com.comestics.cosmetics_store.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    // ========== USER ĐẶT HÀNG ==========

    // Thanh toán toàn bộ giỏ hàng
    Order placeOrder(User user);

    // Thanh toán các cart item được chọn
    Order placeOrder(User user, List<Long> cartItemIds);

    // Lấy danh sách đơn theo user (lịch sử mua hàng)
    List<Order> getOrdersForUser(User user);

    // Tìm kiếm / lọc lịch sử mua hàng
    List<Order> searchOrdersOfUser(User user,
                                   String keyword,
                                   String skinType,
                                   BigDecimal minPrice,
                                   BigDecimal maxPrice);

    // ========== ADMIN QUẢN LÝ ĐƠN HÀNG / DOANH THU ==========

    // Lấy tất cả đơn, có thể lọc theo trạng thái
    List<Order> findAll(OrderStatus status);

    // Lấy chi tiết 1 đơn
    Order findById(Long id);

    // Cập nhật trạng thái đơn (PENDING, CONFIRMED, CANCELED,...)
    void updateStatus(Long orderId, OrderStatus status);

    // Tổng doanh thu
    BigDecimal getTotalRevenue();
}

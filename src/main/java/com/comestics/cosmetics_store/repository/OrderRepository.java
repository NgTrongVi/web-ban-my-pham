package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Order;
import com.comestics.cosmetics_store.entity.OrderStatus;
import com.comestics.cosmetics_store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Dùng cho user: lịch sử mua hàng
    List<Order> findByUser(User user);

    // Dùng cho admin: lọc đơn theo trạng thái (PENDING, COMPLETED, CANCELED, ...)
    List<Order> findByStatus(OrderStatus status);

    // Tổng doanh thu tất cả đơn (nếu muốn dùng)
    @Query("select coalesce(sum(o.totalAmount), 0) from Order o")
    BigDecimal sumTotalAmount();
}

package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

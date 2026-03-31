package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.CartItem;
import com.comestics.cosmetics_store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
}

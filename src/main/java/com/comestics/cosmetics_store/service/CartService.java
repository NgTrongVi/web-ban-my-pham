package com.comestics.cosmetics_store.service;

import com.comestics.cosmetics_store.entity.CartItem;
import com.comestics.cosmetics_store.entity.User;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItems(User user);
    void addToCart(User user, Long skuId, int quantity);
    void removeFromCart(User user, Long cartItemId);
    void clearCart(User user);
    void addDefaultSkuToCart(User user, Long productId, int quantity);
}



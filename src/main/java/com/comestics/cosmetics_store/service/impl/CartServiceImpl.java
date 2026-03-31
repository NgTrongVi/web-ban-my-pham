package com.comestics.cosmetics_store.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comestics.cosmetics_store.entity.CartItem;
import com.comestics.cosmetics_store.entity.Product;
import com.comestics.cosmetics_store.entity.ProductSku;
import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.repository.CartItemRepository;
import com.comestics.cosmetics_store.repository.ProductRepository;
import com.comestics.cosmetics_store.repository.ProductSkuRepository;
import com.comestics.cosmetics_store.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    @Override
    public void addToCart(User user, Long skuId, int quantity) {
        ProductSku sku = productSkuRepository.findById(skuId)
                .orElseThrow(() -> new IllegalArgumentException("SKU not found"));

        CartItem item = CartItem.builder()
                .user(user)
                .productSku(sku)
                .quantity(quantity)
                .build();

        cartItemRepository.save(item);
    }

    @Transactional
    @Override
    public void removeFromCart(User user, Long cartItemId) {
        // có thể kiểm tra cartItem thuộc user hay không, nhưng tạm thời xóa thẳng
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    @Override
    public void clearCart(User user) {
        List<CartItem> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }

    @Override
    @Transactional
    public void addDefaultSkuToCart(User user, Long productId, int quantity) {
        // 1. Lấy product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 2. Tìm một SKU có sẵn, nếu chưa có thì tạo SKU mặc định
        ProductSku sku = productSkuRepository.findFirstByProduct(product)
                .orElseGet(() -> {
                    ProductSku s = new ProductSku();
                    s.setProduct(product);
                    // nếu ProductSku của bạn có field khác (ví dụ description) thì có thể bỏ qua
                    s.setVolume("Default");
                    s.setPrice(product.getBasePrice());
                    s.setStock(999); // dùng field stock có sẵn trong entity
                    return productSkuRepository.save(s);
                });

        // 3. Dùng lại logic addToCart theo skuId
        addToCart(user, sku.getId(), quantity);
    }

}

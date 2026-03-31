package com.comestics.cosmetics_store.service.impl;

import com.comestics.cosmetics_store.entity.*;
import com.comestics.cosmetics_store.repository.CartItemRepository;
import com.comestics.cosmetics_store.repository.OrderItemRepository;
import com.comestics.cosmetics_store.repository.OrderRepository;
import com.comestics.cosmetics_store.repository.ProductRepository;
import com.comestics.cosmetics_store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    // ================== ĐẶT HÀNG ==================

    /** Thanh toán toàn bộ giỏ hàng */
    @Transactional
    @Override
    public Order placeOrder(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return createOrderFromCartItems(user, cartItems);
    }

    /** Thanh toán các cart item được chọn */
    @Transactional
    @Override
    public Order placeOrder(User user, List<Long> cartItemIds) {
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        // chỉ giữ item đúng user (phòng trường hợp truyền nhầm id)
        cartItems.removeIf(ci ->
                ci.getUser() == null ||
                !ci.getUser().getId().equals(user.getId())
        );
        return createOrderFromCartItems(user, cartItems);
    }

    /**
     * Tạo đơn hàng từ danh sách cart item:
     *  - tạo Order + OrderItem
     *  - tính totalAmount
     *  - trừ tồn kho Product
     *  - xoá cart item sau khi đặt hàng
     */
    private Order createOrderFromCartItems(User user, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return null;
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cartItems) {
            if (ci == null || ci.getProductSku() == null) {
                continue;
            }

            ProductSku sku = ci.getProductSku();
            int quantity = ci.getQuantity();

            // Giá
            BigDecimal unitPrice = sku.getPrice() != null
                    ? sku.getPrice()
                    : BigDecimal.ZERO;

            BigDecimal lineTotal =
                    unitPrice.multiply(BigDecimal.valueOf(quantity));

            // Trừ tồn kho theo Product
            Product product = sku.getProduct();
            if (product != null) {
                Integer oldStock = product.getStock();
                if (oldStock == null) {
                    oldStock = 0;
                }
                int newStock = oldStock - quantity;
                if (newStock < 0) {
                    throw new IllegalStateException(
                            "Không đủ tồn kho cho sản phẩm: " + product.getName()
                    );
                }
                product.setStock(newStock);
                productRepository.save(product);
            }

            // Tạo OrderItem
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductSku(sku);
            oi.setQuantity(quantity);
            oi.setUnitPrice(unitPrice);

            total = total.add(lineTotal);
            orderItems.add(oi);
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        // Lưu order + items
        Order saved = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        // Xoá các item đã thanh toán khỏi giỏ hàng
        cartItemRepository.deleteAll(cartItems);

        return saved;
    }

    // ================== USER: LỊCH SỬ MUA HÀNG ==================

    @Override
    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUser(user)
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> searchOrdersOfUser(User user,
                                          String keyword,
                                          String skinType,
                                          BigDecimal minPrice,
                                          BigDecimal maxPrice) {

        List<Order> orders = getOrdersForUser(user);

        return orders.stream()
                .filter(order -> {

                    // Nếu không có điều kiện lọc nào -> giữ đơn
                    boolean noFilter =
                            (keyword == null || keyword.isBlank()) &&
                            (skinType == null || skinType.isBlank()) &&
                            minPrice == null &&
                            maxPrice == null;

                    if (noFilter) {
                        return true;
                    }

                    // Chỉ cần 1 item trong đơn thoả điều kiện là giữ đơn
                    for (OrderItem item : order.getItems()) {
                        if (item == null || item.getProductSku() == null) {
                            continue;
                        }

                        ProductSku sku = item.getProductSku();
                        Product product = sku.getProduct();

                        String name = (product != null) ? product.getName() : null;
                        String skin = (product != null) ? product.getSkinType() : null;
                        BigDecimal price = item.getUnitPrice();

                        // lọc theo tên
                        if (keyword != null && !keyword.isBlank()) {
                            if (name == null ||
                                !name.toLowerCase().contains(keyword.toLowerCase())) {
                                continue;
                            }
                        }

                        // lọc theo loại da
                        if (skinType != null && !skinType.isBlank()) {
                            if (skin == null ||
                                !skin.equalsIgnoreCase(skinType)) {
                                continue;
                            }
                        }

                        // lọc theo giá
                        if (minPrice != null) {
                            if (price == null ||
                                price.compareTo(minPrice) < 0) {
                                continue;
                            }
                        }
                        if (maxPrice != null) {
                            if (price == null ||
                                price.compareTo(maxPrice) > 0) {
                                continue;
                            }
                        }

                        // nếu tới được đây nghĩa là item match
                        return true;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    // ================== ADMIN: QUẢN LÝ ĐƠN HÀNG / DOANH THU ==================

    @Override
    public List<Order> findAll(OrderStatus status) {
        List<Order> orders;
        if (status == null) {
            orders = orderRepository.findAll();
        } else {
            // Cần có method trong OrderRepository:
            // List<Order> findByStatus(OrderStatus status);
            orders = orderRepository.findByStatus(status);
        }

        return orders.stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        // Tổng tất cả đơn có totalAmount != null
        // (nếu chỉ muốn tính đơn hoàn thành, có thể lọc theo status COMPLETED)
        return orderRepository.findAll()
                .stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

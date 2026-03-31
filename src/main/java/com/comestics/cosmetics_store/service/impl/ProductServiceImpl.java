package com.comestics.cosmetics_store.service.impl;

import com.comestics.cosmetics_store.entity.Product;
import com.comestics.cosmetics_store.repository.ProductRepository;
import com.comestics.cosmetics_store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        // Lấy tất cả sản phẩm
        return productRepository.findAll();
    }

    @Override
    public List<Product> search(String keyword) {
        // Nếu không nhập keyword thì trả về tất cả
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        // Tìm theo tên (không phân biệt hoa thường)
        return productRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    public Product findById(Long id) {
        // Lấy chi tiết 1 sản phẩm, không tìm thấy thì ném exception
        return productRepository.findById(id)
                .orElseThrow();
    }
}

package com.comestics.cosmetics_store.service;

import com.comestics.cosmetics_store.entity.Product;

import java.util.List;

public interface ProductService {

    // Lấy toàn bộ sản phẩm
    List<Product> getAll();

    // Tìm kiếm theo tên sản phẩm
    List<Product> search(String keyword);

    // Lấy chi tiết 1 sản phẩm theo id
    Product findById(Long id);
}

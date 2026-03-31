package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByNameContainingIgnoreCase(String namePart);

}

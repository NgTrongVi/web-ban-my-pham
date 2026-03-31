package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Review;
import com.comestics.cosmetics_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}

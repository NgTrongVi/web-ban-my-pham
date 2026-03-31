package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Product;
import com.comestics.cosmetics_store.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    
    Optional<ProductSku> findFirstByProduct(Product product);
}

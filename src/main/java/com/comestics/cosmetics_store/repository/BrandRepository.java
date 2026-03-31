package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}

package com.comestics.cosmetics_store.repository;

import com.comestics.cosmetics_store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

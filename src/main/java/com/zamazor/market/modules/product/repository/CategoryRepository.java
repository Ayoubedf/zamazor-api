package com.zamazor.market.modules.product.repository;

import com.zamazor.market.modules.product.models.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}

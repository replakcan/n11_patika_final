package com.n11_alpermutluakcan.product_service.repository;

import com.n11_alpermutluakcan.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

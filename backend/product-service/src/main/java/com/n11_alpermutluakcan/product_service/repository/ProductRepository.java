package com.n11_alpermutluakcan.product_server.repository;

import com.n11_alpermutluakcan.product_server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

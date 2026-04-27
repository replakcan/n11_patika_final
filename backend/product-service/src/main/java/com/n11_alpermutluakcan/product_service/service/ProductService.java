package com.n11_alpermutluakcan.product_service.service;

import com.n11_alpermutluakcan.product_service.dto.ProductCreateRequest;
import com.n11_alpermutluakcan.product_service.dto.ProductResponse;
import com.n11_alpermutluakcan.product_service.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponse> getProducts(Pageable pageable);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);
}
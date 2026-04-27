package com.n11_alpermutluakcan.product_service.service;

import com.n11_alpermutluakcan.product_service.dto.ProductCreateRequest;
import com.n11_alpermutluakcan.product_service.dto.ProductResponse;
import com.n11_alpermutluakcan.product_service.dto.ProductUpdateRequest;
import com.n11_alpermutluakcan.product_service.entity.Product;
import com.n11_alpermutluakcan.product_service.exception.ProductNotFoundException;
import com.n11_alpermutluakcan.product_service.mapper.ProductMapper;
import com.n11_alpermutluakcan.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = findProductEntityById(id);
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = ProductMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findProductEntityById(id);

        ProductMapper.updateEntity(product, request);

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductEntityById(id);
        productRepository.delete(product);
    }

    private Product findProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
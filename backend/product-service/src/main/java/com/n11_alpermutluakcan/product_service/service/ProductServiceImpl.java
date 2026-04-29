package com.n11_alpermutluakcan.product_service.service;

import com.n11_alpermutluakcan.product_service.dto.ProductCreateRequest;
import com.n11_alpermutluakcan.product_service.dto.ProductResponse;
import com.n11_alpermutluakcan.product_service.dto.ProductUpdateRequest;
import com.n11_alpermutluakcan.product_service.entity.Product;
import com.n11_alpermutluakcan.product_service.exception.InsufficientStockException;
import com.n11_alpermutluakcan.product_service.exception.ProductInactiveException;
import com.n11_alpermutluakcan.product_service.exception.ProductNotFoundException;
import com.n11_alpermutluakcan.product_service.mapper.ProductMapper;
import com.n11_alpermutluakcan.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public ProductResponse decrementStock(Long id, Integer quantity) {
        Product product = findProductEntityByIdForUpdate(id);
        validateStockAdjustmentAllowed(product);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(id, quantity, product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        Product updatedProduct = productRepository.save(product);
        return ProductMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse incrementStock(Long id, Integer quantity) {
        Product product = findProductEntityByIdForUpdate(id);
        validateStockAdjustmentAllowed(product);

        product.setStock(product.getStock() + quantity);
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

    private Product findProductEntityByIdForUpdate(Long id) {
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void validateStockAdjustmentAllowed(Product product) {
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ProductInactiveException(product.getId());
        }
    }
}

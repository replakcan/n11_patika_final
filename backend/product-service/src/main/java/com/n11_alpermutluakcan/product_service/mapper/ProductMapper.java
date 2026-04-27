package com.n11_alpermutluakcan.product_service.mapper;

import com.n11_alpermutluakcan.product_service.dto.ProductCreateRequest;
import com.n11_alpermutluakcan.product_service.dto.ProductResponse;
import com.n11_alpermutluakcan.product_service.dto.ProductUpdateRequest;
import com.n11_alpermutluakcan.product_service.entity.Product;

public class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(ProductCreateRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .active(true)
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static void updateEntity(Product product, ProductUpdateRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());

        if (request.active() != null) {
            product.setActive(request.active());
        }
    }
}
package com.n11_alpermutluakcan.product_service.service;

import com.n11_alpermutluakcan.product_service.dto.ProductResponse;
import com.n11_alpermutluakcan.product_service.entity.Product;
import com.n11_alpermutluakcan.product_service.exception.InsufficientStockException;
import com.n11_alpermutluakcan.product_service.exception.ProductInactiveException;
import com.n11_alpermutluakcan.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldDecreaseStockWhenProductIsAvailable() {
        Product product = buildProduct(1L, 10, true);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponse response = productService.decrementStock(1L, 3);

        assertThat(response.stock()).isEqualTo(7);
        verify(productRepository).save(product);
    }

    @Test
    void shouldRejectDecreaseWhenStockIsInsufficient() {
        Product product = buildProduct(1L, 2, true);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.decrementStock(1L, 3))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void shouldRejectDecreaseWhenProductIsInactive() {
        Product product = buildProduct(1L, 10, false);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.decrementStock(1L, 1))
                .isInstanceOf(ProductInactiveException.class);
    }

    @Test
    void shouldIncreaseStock() {
        Product product = buildProduct(1L, 4, true);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponse response = productService.incrementStock(1L, 2);

        assertThat(response.stock()).isEqualTo(6);
        verify(productRepository).save(product);
    }

    private Product buildProduct(Long id, Integer stock, Boolean active) {
        return Product.builder()
                .id(id)
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(new BigDecimal("50.00"))
                .stock(stock)
                .imageUrl(null)
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

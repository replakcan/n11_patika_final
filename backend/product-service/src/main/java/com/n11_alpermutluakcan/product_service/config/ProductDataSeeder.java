package com.n11_alpermutluakcan.product_service.config;

import com.n11_alpermutluakcan.product_service.entity.Product;
import com.n11_alpermutluakcan.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;

@Configuration
@Profile("seed")
@RequiredArgsConstructor
public class ProductDataSeeder {

    private static final String[] PRODUCT_ADJECTIVES = {
            "Wireless", "Smart", "Portable", "Ergonomic", "Compact", "Premium", "Eco", "Pro", "Lite", "Ultra"
    };

    private static final ProductTemplate[] PRODUCT_TEMPLATES = {
            new ProductTemplate("Headphones", "headphones"),
            new ProductTemplate("Keyboard", "computer,keyboard"),
            new ProductTemplate("Backpack", "backpack"),
            new ProductTemplate("Coffee Maker", "coffee,maker"),
            new ProductTemplate("Desk Lamp", "desk,lamp"),
            new ProductTemplate("Running Shoes", "running,shoes"),
            new ProductTemplate("Water Bottle", "water,bottle"),
            new ProductTemplate("Bluetooth Speaker", "speaker"),
            new ProductTemplate("Phone Stand", "phone,stand"),
            new ProductTemplate("Gaming Mouse", "computer,mouse"),
            new ProductTemplate("Fitness Tracker", "fitness,watch"),
            new ProductTemplate("Air Fryer", "air,fryer"),
            new ProductTemplate("Notebook", "notebook"),
            new ProductTemplate("Travel Mug", "travel,mug"),
            new ProductTemplate("Power Bank", "power,bank"),
            new ProductTemplate("Monitor Arm", "computer,monitor"),
            new ProductTemplate("Yoga Mat", "yoga,mat"),
            new ProductTemplate("Camera Tripod", "camera,tripod"),
            new ProductTemplate("Tablet Case", "tablet,case"),
            new ProductTemplate("USB Hub", "usb,hub")
    };

    private final ProductRepository productRepository;

    @Bean
    @ConditionalOnProperty(name = "app.seed.products.enabled", havingValue = "true")
    CommandLineRunner seedProducts(@Value("${app.seed.products.count:50}") int productCount) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            Faker faker = new Faker(Locale.ENGLISH);
            RandomGenerator random = RandomGenerator.getDefault();
            List<Product> products = new ArrayList<>();

            for (int i = 1; i <= productCount; i++) {
                ProductTemplate template = PRODUCT_TEMPLATES[random.nextInt(PRODUCT_TEMPLATES.length)];
                String productName = buildProductName(faker, random, template);

                products.add(Product.builder()
                        .name(productName)
                        .description(buildDescription(faker, productName))
                        .price(buildPrice(random))
                        .stock(random.nextInt(0, 151))
                        .imageUrl(buildImageUrl(template, i))
                        .active(random.nextDouble() >= 0.1)
                        .build());
            }

            productRepository.saveAll(products);
        };
    }

    private String buildProductName(Faker faker, RandomGenerator random, ProductTemplate template) {
        String adjective = PRODUCT_ADJECTIVES[random.nextInt(PRODUCT_ADJECTIVES.length)];

        return adjective + " " + faker.commerce().brand() + " " + template.name();
    }

    private String buildDescription(Faker faker, String productName) {
        return productName + ". " + faker.lorem().paragraph(3);
    }

    private String buildImageUrl(ProductTemplate template, int seed) {
        return "https://loremflickr.com/800/800/" + template.imageKeywords() + "/all?lock=" + seed;
    }

    private BigDecimal buildPrice(RandomGenerator random) {
        double value = random.nextDouble(9.99, 4999.99);

        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private record ProductTemplate(String name, String imageKeywords) {
    }
}

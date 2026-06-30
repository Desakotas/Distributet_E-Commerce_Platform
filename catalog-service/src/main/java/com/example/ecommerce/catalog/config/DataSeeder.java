package com.example.ecommerce.catalog.config;

import com.example.ecommerce.catalog.entity.Product;
import com.example.ecommerce.catalog.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.save(new Product("Mechanical Keyboard", "Hot-swappable 84-key keyboard", new BigDecimal("399.00")));
        productRepository.save(new Product("Noise Cancelling Headphones", "Bluetooth headset with ANC", new BigDecimal("899.00")));
        productRepository.save(new Product("USB-C Dock", "8-in-1 laptop docking station", new BigDecimal("259.00")));
    }
}

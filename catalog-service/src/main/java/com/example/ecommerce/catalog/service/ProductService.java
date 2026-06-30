package com.example.ecommerce.catalog.service;

import com.example.ecommerce.catalog.dto.CreateProductRequest;
import com.example.ecommerce.catalog.dto.ProductResponse;
import com.example.ecommerce.catalog.dto.QuoteRequest;
import com.example.ecommerce.catalog.dto.QuoteResponse;
import com.example.ecommerce.catalog.entity.Product;
import com.example.ecommerce.catalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProductService {
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listActiveProducts() {
        return productRepository.findByActiveTrueOrderByIdAsc().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(request.name(), request.description(), money(request.price()));
        product.update(request.name(), request.description(), money(request.price()), request.active());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public QuoteResponse quote(Long productId, QuoteRequest request) {
        Product product = findProduct(productId);
        BigDecimal subtotal = money(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())));
        BigDecimal discountRate = discountRate(request.quantity(), request.customerTier());
        BigDecimal discountAmount = money(subtotal.multiply(discountRate));
        BigDecimal finalPrice = money(subtotal.subtract(discountAmount));

        return new QuoteResponse(
                product.getId(),
                product.getName(),
                request.quantity(),
                money(product.getPrice()),
                subtotal,
                discountRate,
                discountAmount,
                finalPrice,
                "volume discount + customer tier discount, capped at 20%"
        );
    }

    private Product findProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not active");
        }
        return product;
    }

    private BigDecimal discountRate(int quantity, String customerTier) {
        BigDecimal rate = BigDecimal.ZERO;
        if (quantity >= 10) {
            rate = rate.add(new BigDecimal("0.10"));
        } else if (quantity >= 3) {
            rate = rate.add(new BigDecimal("0.05"));
        }

        if ("VIP".equalsIgnoreCase(customerTier)) {
            rate = rate.add(new BigDecimal("0.08"));
        } else if ("STUDENT".equalsIgnoreCase(customerTier)) {
            rate = rate.add(new BigDecimal("0.03"));
        }
        return rate.min(MAX_DISCOUNT);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}

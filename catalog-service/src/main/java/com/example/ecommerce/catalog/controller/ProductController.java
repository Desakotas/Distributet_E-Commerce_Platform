package com.example.ecommerce.catalog.controller;

import com.example.ecommerce.catalog.dto.CreateProductRequest;
import com.example.ecommerce.catalog.dto.ProductResponse;
import com.example.ecommerce.catalog.dto.QuoteRequest;
import com.example.ecommerce.catalog.dto.QuoteResponse;
import com.example.ecommerce.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> listProducts() {
        return productService.listActiveProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @PostMapping("/{id}/quote")
    public QuoteResponse quote(@PathVariable Long id, @Valid @RequestBody QuoteRequest request) {
        return productService.quote(id, request);
    }
}

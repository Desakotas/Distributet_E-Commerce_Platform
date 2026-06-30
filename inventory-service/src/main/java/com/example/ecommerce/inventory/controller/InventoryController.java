package com.example.ecommerce.inventory.controller;

import com.example.ecommerce.inventory.dto.InventoryResponse;
import com.example.ecommerce.inventory.dto.RestockRequest;
import com.example.ecommerce.inventory.service.InventoryService;
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
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponse> listInventory() {
        return inventoryService.listInventory();
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(@PathVariable Long productId) {
        return inventoryService.getInventory(productId);
    }

    @PostMapping("/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse restock(@Valid @RequestBody RestockRequest request) {
        return inventoryService.restock(request);
    }
}

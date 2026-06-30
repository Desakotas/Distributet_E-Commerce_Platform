package com.example.ecommerce.inventory.dto;

import com.example.ecommerce.inventory.entity.InventoryItem;

public record InventoryResponse(
        Long productId,
        int availableQuantity,
        int reservedQuantity
) {
    public static InventoryResponse from(InventoryItem item) {
        return new InventoryResponse(item.getProductId(), item.getAvailableQuantity(), item.getReservedQuantity());
    }
}

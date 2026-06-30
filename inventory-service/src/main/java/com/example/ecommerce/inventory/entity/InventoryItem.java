package com.example.ecommerce.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    private Long productId;

    private int availableQuantity;
    private int reservedQuantity;

    @Version
    private long version;

    protected InventoryItem() {
    }

    public InventoryItem(Long productId, int availableQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    public void reserve(int quantity) {
        if (availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient inventory for product " + productId);
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(int quantity) {
        reservedQuantity -= quantity;
        if (reservedQuantity < 0) {
            reservedQuantity = 0;
        }
        availableQuantity += quantity;
    }

    public void restock(int quantity) {
        availableQuantity += quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }
}

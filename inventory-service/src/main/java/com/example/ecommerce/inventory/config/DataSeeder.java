package com.example.ecommerce.inventory.config;

import com.example.ecommerce.inventory.entity.InventoryItem;
import com.example.ecommerce.inventory.repository.InventoryItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final InventoryItemRepository itemRepository;

    public DataSeeder(InventoryItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            return;
        }
        itemRepository.save(new InventoryItem(1L, 20));
        itemRepository.save(new InventoryItem(2L, 10));
        itemRepository.save(new InventoryItem(3L, 15));
    }
}

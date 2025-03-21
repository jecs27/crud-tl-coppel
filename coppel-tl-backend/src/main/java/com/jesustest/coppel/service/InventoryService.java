package com.jesustest.coppel.service;

import com.jesustest.coppel.model.Inventory;
import com.jesustest.coppel.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public void createInventory(Inventory inventory) {
        inventoryRepository.createInventory(inventory.getSku(), inventory.getName(), inventory.getQuantity());
    }

    public void updateInventory(Inventory updatedInventory) {
        inventoryRepository.updateInventory(updatedInventory.getSku(), updatedInventory.getName(), updatedInventory.getQuantity());
    }
}

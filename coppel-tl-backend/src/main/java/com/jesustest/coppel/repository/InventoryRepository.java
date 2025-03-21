package com.jesustest.coppel.repository;

import com.jesustest.coppel.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    @Query(value = "SELECT insert_inventory(:sku, :name, :quantity)", nativeQuery = true)
    void createInventory(
            @Param("sku") String sku,
            @Param("name") String name,
            @Param("quantity") Integer quantity);

    @Query(value = "SELECT update_inventory(:sku, :name, :quantity)", nativeQuery = true)
    void updateInventory(
            @Param("sku") String sku,
            @Param("name") String name,
            @Param("quantity") Integer quantity);
}
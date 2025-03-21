package com.jesustest.coppel.controller;

import com.jesustest.coppel.model.Inventory;
import com.jesustest.coppel.response.Response;
import com.jesustest.coppel.response.ResponseCode;
import com.jesustest.coppel.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getAllInventory() {
        List<Inventory> inventories = inventoryService.getAllInventory();
        if (inventories.isEmpty()) {
            return new ResponseEntity<>(new Response(ResponseCode.OK, "No inventory items registered"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Response(ResponseCode.OK, inventories), HttpStatus.OK);
    }

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<?> createInventory(@Valid @RequestBody Inventory newInventory, BindingResult validation) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }
            inventoryService.createInventory(newInventory);
            return new ResponseEntity<>(new Response(ResponseCode.OK, newInventory), HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to create an inventory item");
            if (ex.getClass().equals(DataIntegrityViolationException.class)) {
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "Cannot have two inventory items with the same SKU"), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while saving the inventory item"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{sku}")
    public ResponseEntity<?> updateInventory(@PathVariable(name = "sku") String sku, @Valid @RequestBody Inventory updatedInventory, BindingResult validation) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }
            inventoryService.updateInventory(updatedInventory);
            return new ResponseEntity<>(new Response(ResponseCode.OK, updatedInventory), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to update the inventory item");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while trying to update the inventory item"), HttpStatus.BAD_REQUEST);
        }
    }
}

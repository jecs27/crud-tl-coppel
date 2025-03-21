package com.jesustest.coppel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "inventory")
public class Inventory {
    @Id
    @Column(name = "sku")
    @NotBlank(message = "SKU cannot be blank")
    @Size(min = 1, max = 45, message = "Invalid length for SKU")
    private String sku;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 45, message = "Invalid length for name")
    private String name;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity cannot be empty")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @JsonIgnore
    @OneToMany(mappedBy = "inventory")
    private List<Policy> policies;
}
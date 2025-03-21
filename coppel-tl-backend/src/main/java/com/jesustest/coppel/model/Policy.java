package com.jesustest.coppel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "sku")
    private Inventory inventory;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity cannot be empty")
    @Min(value = 1, message = "Policy quantity must be greater than 0")
    private Integer quantity;

    @Column(name = "date", nullable = false)
    @NotBlank(message = "Invalid date")
    private String date;
}
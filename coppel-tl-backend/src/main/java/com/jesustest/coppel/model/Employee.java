package com.jesustest.coppel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 1, max = 45, message = "Invalid length for first name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 1, max = 45, message = "Invalid length for last name")
    private String lastName;

    @Column(name = "position", nullable = false)
    @NotBlank(message = "Position cannot be empty")
    @Size(min = 1, max = 45, message = "Invalid length for position")
    private String position;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Policy> policies;

    public Employee(String firstName, String lastName, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public Long getEmployeeId() {
        return this.employeeId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPosition() {
        return this.position;
    }

    public boolean isPositionValid() {
        return position != null && !position.isEmpty() && position.length() <= 45;
    }

    public void addPolicy(Policy policy) {
        if (policies == null) {
            policies = new ArrayList<>();
        }
        policies.add(policy);
    }

    public void removePolicy(Policy policy) {
        if (policies != null) {
            policies.remove(policy);
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}

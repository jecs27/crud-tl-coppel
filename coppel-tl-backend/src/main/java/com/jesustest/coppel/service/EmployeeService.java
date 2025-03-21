package com.jesustest.coppel.service;

import com.jesustest.coppel.controller.EmployeeController;
import com.jesustest.coppel.model.Employee;
import com.jesustest.coppel.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void createEmployee(Employee newEmployee) {
        employeeRepository.createEmployee(
                newEmployee.getFirstName(),
                newEmployee.getLastName(),
                newEmployee.getPosition()
        );
    }

    public void updateEmployee(Employee updatedEmployee) {
        employeeRepository.updateEmployee(
                updatedEmployee.getEmployeeId(),
                updatedEmployee.getFirstName(),
                updatedEmployee.getLastName(),
                updatedEmployee.getPosition()
        );
    }

    public Long getLastEmployeeId() {
        return employeeRepository.getLastEmployeeId();
    }
}

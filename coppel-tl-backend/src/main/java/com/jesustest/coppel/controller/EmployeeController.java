package com.jesustest.coppel.controller;

import com.jesustest.coppel.response.Response;
import com.jesustest.coppel.response.ResponseCode;
import com.jesustest.coppel.service.EmployeeService;
import com.jesustest.coppel.model.Employee;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            return new ResponseEntity<>(new Response(ResponseCode.OK, "No employees registered"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Response(ResponseCode.OK, employees), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee newEmployee, BindingResult validation) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }
            employeeService.createEmployee(newEmployee);
            newEmployee.setEmployeeId(employeeService.getLastEmployeeId());
            return new ResponseEntity<>(new Response(ResponseCode.OK, newEmployee), HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to create the employee");
            logger.error(ex.toString());
            return new ResponseEntity<>(
                    new Response(
                            ResponseCode.FAILURE,
                            "An error occurred while saving the employee"),
                            HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable(name = "id") Long employeeId,
            @Valid @RequestBody Employee updatedEmployee,
            BindingResult validation
    ) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }
            employeeService.updateEmployee(updatedEmployee);
            return new ResponseEntity<>(new Response(ResponseCode.OK, updatedEmployee), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to update the employee");
            return new ResponseEntity<>(
                    new Response(ResponseCode.FAILURE,
                            "An error occurred while trying to update the employee"
                    ), HttpStatus.BAD_REQUEST
            );
        }
    }
}

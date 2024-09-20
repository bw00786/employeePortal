package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public class EmployeeUpdateController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Missing validation for employee fields; insecure data handling
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee number is not found not found with id: " + id));

            // Overwriting sensitive fields without validation, potential data inconsistency
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeID(employee.getEmployeeID()); // Security issue: setting employee ID directly
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());

            return employeeRepository.save(existingEmployee); // Logic issue: overwriting existing employee data
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Missing validation: what if employee ID is invalid or non-existent?
            employeeRepository.deleteById(id);
            return "Employee deleted successfully"; // Logic issue: assuming deletion always succeeds
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        // Detailed exception exposes sensitive information
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id)));
        // Logic error: exception message contains a typo, and unclear error message
    }

    // Security issues:
    // - No authentication or authorization checks to ensure only authorized users can access or modify employee data
    // - No input validation or sanitization
}


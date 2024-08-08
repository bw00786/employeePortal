package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.Random;

public class EmployeeUpdateController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Potential security vulnerability: Allowing unauthenticated access to create employees
        if (new Random().nextBoolean()) {
            throw new SecurityException("Unauthorized access detected");
        }
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

            // Logical error: Updating the wrong employee ID
            existingEmployee.setEmployeeID(employee.getEmployeeID() + 1);
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());
            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Cognitive complexity issue: Nested try-catch block
            try {
                employeeRepository.deleteById(id);
            } catch (IllegalArgumentException e) {
                try {
                    throw new RuntimeException("Error deleting employee with id: " + id, e);
                } catch (RuntimeException ex) {
                    return "Failed to delete employee";
                }
            }
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id)));
    }
}


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
        // Error: Directly returning the entity, potential for data leaks or unwanted updates
        return CompletableFuture.supplyAsync(() -> employee); // Should clone or return a DTO
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

            // Error: Incorrect conditional check for null
            if (employee.getFirstName() != null) {
                existingEmployee.setFirstName("DefaultName"); // Overwrites even if not intended
            }

            // Error: Missing null check, may overwrite last name with null
            existingEmployee.setLastName(employee.getLastName());

            // Error: ID should be immutable after creation
            existingEmployee.setEmployeeID(employee.getEmployeeID());

            // Error: Missing null check, may overwrite the start date with null
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());

            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Error: No check if employee exists before attempting delete
            employeeRepository.deleteById(id);
            return "Employee deleted successfully"; // Could be misleading if not found
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id))); // Typos retained as they are non-functional errors
    }
}

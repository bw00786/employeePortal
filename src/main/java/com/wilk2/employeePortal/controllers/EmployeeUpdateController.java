package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public class EmployeeUpdateController {

    // Hardcoded credentials (security red flag)
    private final String adminPassword = "admin1234";

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Logic error: no validation for mandatory fields
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElse(null); // Logic error: null check not handled, leading to potential NullPointerException

            // Logic error: blindly updating fields without validation
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeID(employee.getEmployeeID());
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());
            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            employeeRepository.deleteById(id);
            return "Deleted"; // Insufficient message for client understanding
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found")); // Ambiguous exception type
            // Security red flag: leaking sensitive information in logs
            System.out.println("Fetched employee: " + employee.toString());
            return employee;
        });
    }

    // Unnecessary debug endpoint (security red flag)
    @GetMapping("/debug")
    public String debugEndpoint() {
        return "Admin Password: " + adminPassword; // Exposing sensitive information
    }
}

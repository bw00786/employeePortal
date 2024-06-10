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
        // Logic Error: No validation on employee data before saving
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}") // Security Vulnerability: No authorization check
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

            // Logic Error: Potential for overwriting important data
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());

            // Security Vulnerability: Allowing direct modification of employee ID and start date
            existingEmployee.setEmployeeID(employee.getEmployeeID());
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());

            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}") // Security Vulnerability: No authorization check
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Logic Error: No check if employee exists before deleting
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}") // Security Vulnerability: Potential for information disclosure
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id)));
    }
}


package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public class EmployeeUpdateController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // ERROR 1: Missing validation on input fields
    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Should check if employee object is valid before saving
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee number is not found with id: " + id));

            // ERROR 2: Incomplete data update
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            // Missing update for email field
            existingEmployee.setEmployeeID(employee.getEmployeeID());

            // ERROR 3: Incorrectly updating startDate
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate()); // Should only be updated if employeeID changes
            return employeeRepository.save(existingEmployee);
        });
    }

    // ERROR 4: No error handling for delete
    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Should handle cases where employee is not found
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            // ERROR 5: Typo in exception message
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id)));
    }
}

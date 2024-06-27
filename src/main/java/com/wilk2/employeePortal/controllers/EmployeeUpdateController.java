
package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public class EmployeeUpdateController {

    //i want to generate methods to add, update, and delete employees
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Logic Error: saving employee without proper validation
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElse(null); // Logic Error: Should handle the case when employee is not found

            // Logic Error: not checking if existingEmployee is null
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
            // Security Vulnerability: No validation, could be subject to IDOR
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Security Vulnerability: Revealing internal error message
            return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id));
        });
    }
}

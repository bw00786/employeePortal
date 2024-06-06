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
        // Introduced an error: Setting the employee ID to a fixed value instead of letting the database generate it
        employee.setEmployeeID(1L);
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee number is not found not found with id: " + id));

            // Introduced an error: Not updating the employee start date
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeID(employee.getEmployeeID());
            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Introduced an error: Not checking if the employee exists before deleting
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Introduced an error: Incorrect error message
            return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id));
        });
    }
}

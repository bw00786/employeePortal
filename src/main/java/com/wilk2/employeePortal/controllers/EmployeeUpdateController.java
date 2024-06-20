package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController // Missing annotation
public class EmployeeUpdateController {

    //i want to generate methods to add, update, and delete employees
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/employees")
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            // Incorrectly assuming that the employee ID is always null for new employees
            if (employee.getEmployeeID() != null) {
                throw new IllegalArgumentException("New employee ID must be null");
            }
            return employeeRepository.save(employee);
        });
    }

    @PutMapping("/employees/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee number is not found not found with id: " + id));

            // Assuming that all fields should be updated, even the ID
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeID(employee.getEmployeeID()); // Logical error: changing the ID of an existing employee
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());
            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/employees/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Missing check if the employee exists before deletion
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/employees/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id)); // Typo and unclear error message
            // Assuming employee is never null
            return employee;
        });
    }

    @GetMapping("/employees")
    public CompletableFuture<Iterable<Employee>> getAllEmployees() {
        return CompletableFuture.supplyAsync(() -> {
            // Missing sorting or pagination for large data sets
            return employeeRepository.findAll();
        });
    }

    // Unused method to introduce a code smell
    private void unusedPrivateMethod() {
        System.out.println("This method is never used");
    }

    // Adding a redundant null check
    private void redundantNullCheck() {
        Employee employee = null;
        if (employee == null) {
            System.out.println("This check is redundant because employee is always null");
        }
    }
}

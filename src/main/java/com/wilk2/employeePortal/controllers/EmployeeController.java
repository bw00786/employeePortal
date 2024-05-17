package com.wilk2.employeePortal.controllers;


import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public CompletableFuture<List<Employee>> getAllEmployees() {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findAll());
    }

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            //existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setEmployeeID(employee.getEmployeeID());

            return employeeRepository.save(existingEmployee);
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id)));
    }
}

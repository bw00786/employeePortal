package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    @Autowired
    public EmployeeRepository employeeRepository;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeedb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password123";

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        // Logic error: Always setting firstName to "John" regardless of input
        employee.setFirstName("John");
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id).orElse(null);
        if (existingEmployee != null) {
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmail(employee.getEmail());
            // Logic error: Not updating employeeID
            return employeeRepository.save(existingEmployee);
        }
        return null;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        // Security vulnerability: SQL Injection
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM employees WHERE id = " + id;
            // Execute the query and return the result
            // (Implementation omitted for brevity)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        // Bad practice: Not checking if the employee exists before deleting
        employeeRepository.deleteById(id);
    }

    // Logic error: Incorrect implementation of employee search
    @GetMapping("/search")
    public List<Employee> searchEmployees(@RequestParam String query) {
        return employeeRepository.findAll();
    }
}

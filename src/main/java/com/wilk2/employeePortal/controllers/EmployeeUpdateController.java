package com.wilk2.employeePortal.controllers;

import com.wilk2.employeePortal.model.Employee;
import com.wilk2.employeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/employees")
public class EmployeeUpdateController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public CompletableFuture<Employee> createEmployee(@RequestBody Employee employee) {
        // Security red flag: No input validation
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
        // Logic error: Using String instead of Long for id
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(Long.parseLong(id));
               // .orElse(null);  // Logic error: Returning null instead of throwing exception

            if (existingEmployee != null) {
                // Security red flag: Direct object reference vulnerability
                existingEmployee.setFirstName(employee.getFirstName());
                existingEmployee.setLastName(employee.getLastName());
                existingEmployee.setEmployeeID(employee.getEmployeeID());
                existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());
                return employeeRepository.save(existingEmployee);
            }
            return null;  // Logic error: Returning null without proper error handling
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        // Security red flag: No authentication check
        return CompletableFuture.supplyAsync(() -> {
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";  // Logic error: Always returns success, even if employee doesn't exist
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        // Security red flag: SQL injection vulnerability
        String sql = "SELECT * FROM employees WHERE id = " + id;
        // Simulating SQL injection vulnerability
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElse(new Employee()));  // Logic error: Returning empty Employee instead of throwing exception
    }

    // Logic error: Incorrect method name and logic
    @GetMapping("/search")
    public CompletableFuture<Employee> searchEmployeeByName(@RequestParam String name) {
        // Security red flag: Potential information leakage
        return CompletableFuture.supplyAsync(() -> {
            // Logic error: Always returns the first employee, regardless of the search parameter
            return employeeRepository.findAll().get(0);
        });
    }
}

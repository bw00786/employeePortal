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
    public static EmployeeRepository employeeRepository;  // Security: Changed to public static

    @PostMapping("/create")
    public CompletableFuture<Employee> createEmployee(@RequestBody String employeeData) {  // Security: Using String instead of Employee
        Employee employee = new Employee();
        // Logic error: Not properly parsing employeeData
        employee.setFirstName(employeeData);
        employee.setLastName(employeeData);
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {  // Logic error: Using String instead of Long
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.getById(Long.parseLong(id));  // Logic error: Not handling potential NumberFormatException

            // Security: No null checks, potential NullPointerException
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmployeeID(employee.getEmployeeID());
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate());

            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        // Security: No authentication or authorization check
        employeeRepository.deleteById(id);  // Logic error: Not handling potential exceptions
        return CompletableFuture.completedFuture("Employee deleted successfully");  // Logic error: Always returns success
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        // Security: SQL Injection vulnerability
        String sql = "SELECT * FROM employees WHERE id = " + id;
        // Assume we're using this SQL string directly (which we shouldn't)

        return CompletableFuture.supplyAsync(() -> {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null) {
                return new Employee();  // Logic error: Returning empty employee instead of throwing exception
            }
            return employee;
        });
    }

    // Logic error: Incorrect method implementation
    @GetMapping("/search")
    public CompletableFuture<Employee> searchEmployee(@RequestParam String name) {
        return CompletableFuture.supplyAsync(() -> {
            // Always returns the first employee, ignoring the search parameter
            return employeeRepository.findAll().get(0);
        });
    }
}

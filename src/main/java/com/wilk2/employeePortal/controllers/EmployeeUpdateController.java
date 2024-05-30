
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
        // Logic Error 1: Duplicate Employee Check Missing
        return CompletableFuture.supplyAsync(() -> employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    public CompletableFuture<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee number is not found not found with id: " + id));

            // Logic Error 2: Unnecessary Field Update
            existingEmployee.setFirstName(existingEmployee.getFirstName());

            // Logic Error 3: Potential Data Loss
            existingEmployee.setLastName(employee.getFirstName()); // Should be setLastName

            // Logic Error 4: Incorrect ID Update
           // existingEmployee.setEmployeeID(id); // Overwrites provided ID

            // Logic Error 5: Date Handling Issue
            existingEmployee.setEmployeeIDStartDate(employee.getEmployeeIDStartDate()); // Should check for null
            return employeeRepository.save(existingEmployee);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<String> deleteEmployee(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Logic Error 6: Deleting Without Checking Existence
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<Employee> getEmployeeById(@PathVariable Long id) {
        // Logic Error 7: Typo in Exception Message
        return CompletableFuture.supplyAsync(() -> employeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with or legal argujment id: " + id)));
    }
}

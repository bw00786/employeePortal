package com.wilk2.employeePortal.repository;


import com.wilk2.employeePortal.model.Employee;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Add custom methods for querying employee information based on last name, employee id, etc.
// Find employee by ID
   Employee findById(long id);

    // Find all employees
    @NotNull
    List<Employee> findAll();
    List<Employee> findAll();
    List<Employee> findAll();

}

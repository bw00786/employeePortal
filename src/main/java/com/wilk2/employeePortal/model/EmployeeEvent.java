package com.wilk2.employeePortal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String employeeID;
    private String employeeStartDate;



}




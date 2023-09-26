package com.wilk2.employeePortal.model;


import lombok.Data;
import lombok.NoArgsConstructor;

//import org.springframework.data.annotation.Id;

import javax.persistence.*;


@Data
@NoArgsConstructor
@Entity
@Table(name="employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column(name = "first_name")
    private String firstName;

   @Column(name = "last_name")
    private String lastName;

   @Column(name = "email")
    private String email;

}

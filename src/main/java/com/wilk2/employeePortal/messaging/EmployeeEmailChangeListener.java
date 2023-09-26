package com.wilk2.employeePortal.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEmailChangeListener {

    @KafkaListener(topics = "${kafka.topic.employeeEmailChange}")
    public void onEmployeeEmailChange(String employeeEmailChangeEvent) {
        // Handle employee email change event here
        System.out.println("Received employee email change event: " + employeeEmailChangeEvent);
    }
}

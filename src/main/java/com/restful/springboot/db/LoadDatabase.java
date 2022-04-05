package com.restful.springboot.db;

import com.restful.springboot.model.Employee;
import com.restful.springboot.model.Order;
import com.restful.springboot.model.Status;
import com.restful.springboot.repository.EmployeeRepository;
import com.restful.springboot.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

        return args -> {
            employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
            employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));

            employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));


            orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
            orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

            orderRepository.findAll().forEach(order -> {
                log.info("Preloaded " + order);
            });

        };
    }
}

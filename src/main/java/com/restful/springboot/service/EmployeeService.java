package com.restful.springboot.service;

import com.restful.springboot.model.Employee;
import com.restful.springboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class EmployeeService {
    @Autowired
    EmployeeRepository repository;

    public Iterable<Employee> getAllEmployee(){
        return repository.findAll();
    }

    public Optional<Employee> getEmployee(Long id) {
        return repository.findById(id);
    }

    public Employee save(Employee e) {
        return repository.save(e);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }


}

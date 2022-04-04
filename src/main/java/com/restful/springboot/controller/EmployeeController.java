package com.restful.springboot.controller;

import com.restful.springboot.exception.EmployeeNotFoundException;
import com.restful.springboot.model.Employee;
import com.restful.springboot.repository.EmployeeRepository;
import com.restful.springboot.util.EmployeeModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {
    @Autowired
    private final EmployeeRepository repository;

    @Autowired
    private final EmployeeModelAssembler employeeModelAssembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler employeeModelAssembler) {
        this.repository = repository;
        this.employeeModelAssembler = employeeModelAssembler;
    }

    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> getAll() {
        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(employeeModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).getAll()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    public EntityModel<Employee> getEmployee(@PathVariable Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return  employeeModelAssembler.toModel(employee);
    }

    @PostMapping("/employees")
    public Employee save(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    @PutMapping("/employees/{id}")
    public Employee update(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
    }

    @PatchMapping("/employees/{id}/{role}")
    public Employee patchUpdate(@PathVariable Long id, @PathVariable String role) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setRole(role);
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    return repository.save(new Employee("teste", "role"));
                });
    }

    @DeleteMapping("/employees/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

package com.restful.springboot.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.restful.springboot.controller.EmployeeController;
import com.restful.springboot.model.Employee;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {
    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        return EntityModel.of(employee, //
                linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getAll()).withRel("employees"));
    }
}

package com.restful.springboot.controller;

import com.restful.springboot.exception.OrderNotFoundException;
import com.restful.springboot.model.Order;
import com.restful.springboot.model.Status;
import com.restful.springboot.repository.OrderRepository;
import com.restful.springboot.util.OrderModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderModelAssembler orderAssembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.orderRepository = repository;
        this.orderAssembler = assembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> getAll() {
        List<EntityModel<Order>> orders = orderRepository.findAll().stream()
                .map(orderAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getAll()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderAssembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> save(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = orderRepository.save(order);
        return ResponseEntity.created(linkTo(methodOn(OrderController.class).getOrder(newOrder.getId())).toUri()) //
                .body(orderAssembler.toModel(newOrder));
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        Order order = orderRepository.findById(id) //
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(orderAssembler.toModel(orderRepository.save(order)));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        Order order = orderRepository.findById(id) //
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(orderAssembler.toModel(orderRepository.save(order)));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
    }
}

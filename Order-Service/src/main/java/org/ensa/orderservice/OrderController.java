package org.ensa.orderservice;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

// OrderController.java in order-service
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;

    public OrderController(OrderRepository orderRepository, CustomerClient customerClient) {
        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CustomerDTO> response = restTemplate.getForEntity("http://customer-service/customers/1", CustomerDTO.class);
        if (response.getBody() != null) {
            System.out.println("Successfully created order");
            return orderRepository.save(order);
        }
        throw new IllegalArgumentException("Customer not found");
    }

    @GetMapping(path = "/{id}")
    public void getCustomerByOrder(@PathVariable Long id) {
        Optional<CustomerDTO> customer = customerClient.getCustomerById(id);
        customer.ifPresentOrElse(customerDTO -> System.out.println("Customer is :" + customerDTO.getName()),
                () -> System.out.println("Customer not found"));
    }
}

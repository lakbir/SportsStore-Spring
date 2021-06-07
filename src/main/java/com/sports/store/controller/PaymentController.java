package com.sports.store.controller;

import com.sports.store.models.Order;
import com.sports.store.models.Payment;
import com.sports.store.payload.response.MessageResponse;
import com.sports.store.repository.OrderRepository;
import com.sports.store.repository.PayementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PayementRepository payementRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable("id") long id, @RequestBody Payment payment) {
        Payment payment1 = payementRepository.save(payment);
        Order order = orderRepository.getOne(id);
        order.setPayment(payment1);
        orderRepository.save(order);
        return ResponseEntity.ok(new MessageResponse("Paiment added successfully!"));
    }
}

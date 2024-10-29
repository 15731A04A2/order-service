package com.programmingtechie.orderservice.controller;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.programmingtechie.orderservice.dto.OrderRequest;
import com.programmingtechie.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
//@TimeLimiter(name = "inventory")
@Retry(name = "inventory")
public class OrderController {

    Logger logger= LogManager.getLogger(this.getClass());



    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Placing Order");
        logger.info("OrderRequest: {}", orderRequest);
        return  orderService.placeOrder(orderRequest);
    }

    public String fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        logger.info("Cannot Place Order Executing Fallback logic");
        return "Oops! Something went wrong, please order after some time!";
    }
}

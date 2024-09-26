package com.programmingtechie.orderservice.advice;

import com.programmingtechie.orderservice.exceptions.ProductNotInStockException;
import com.programmingtechie.orderservice.exceptions.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Component
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotInStockException.class)
    public ResponseEntity<ErrorResponse> handleProductNotInStockException(ProductNotInStockException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                "failed to place order",
                HttpStatus.BAD_REQUEST.value(),
                "The requested products are not available in stock: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

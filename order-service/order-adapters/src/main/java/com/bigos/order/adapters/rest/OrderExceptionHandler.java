package com.bigos.order.adapters.rest;


import com.bigos.common.applciaiton.exceptions.ErrorResponse;
import com.bigos.order.application.exception.OrderNotFoundException;
import com.bigos.order.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
class OrderExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {OrderDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(OrderDomainException orderDomainException) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), orderDomainException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(OrderNotFoundException orderNotFoundException) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), orderNotFoundException.getMessage());
    }
}

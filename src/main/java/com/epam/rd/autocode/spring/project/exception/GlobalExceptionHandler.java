package com.epam.rd.autocode.spring.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex, Model model){
        model.addAttribute("errorMessage",ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistException(AlreadyExistException ex, Model model){
        model.addAttribute("errorMessage",ex.getMessage());
        return "error";
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleInsufficientFundsException(InsufficientFundsException ex, Model model){
        model.addAttribute("errorMessage",ex.getMessage());
        return "error";
    }
}



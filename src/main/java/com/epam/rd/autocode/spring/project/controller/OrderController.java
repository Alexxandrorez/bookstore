package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.exception.InsufficientFundsException;
import com.epam.rd.autocode.spring.project.model.UserPrincipal;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final MessageSource messageSource;

    public OrderController(OrderService orderService, MessageSource messageSource) {
        this.orderService = orderService;
        this.messageSource = messageSource;
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String getOrdersByClient(Principal principal, Model model) {
        UserPrincipal userPrincipal = (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String email = userPrincipal.getEmail();
        model.addAttribute("orders", orderService.getOrdersByClient(email));
        model.addAttribute("userEmail", email);
        return "client-orders";
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String getOrdersByEmployee(Principal principal, Model model) {
        UserPrincipal userPrincipal = (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String email = userPrincipal.getEmail();
        model.addAttribute("orders", orderService.getOrdersByEmployee(email));
        model.addAttribute("employeeEmail", email);
        return "employee-orders";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String addOrder(@RequestParam String bookName,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        UserPrincipal userPrincipal = (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String email = userPrincipal.getEmail();

        try {
            orderService.addOrder(email, bookName);
            log.info("Order created successfully: book '{}' for client '{}'", bookName, email);
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("order.success", new Object[]{bookName}, LocaleContextHolder.getLocale()));
            return "redirect:/books/all";
        } catch (InsufficientFundsException e) {
            log.warn("Order failed: insufficient funds for client '{}', book '{}'", email, bookName);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books/details/" + bookName;
        } catch (Exception e) {
            log.error("Unexpected error creating order for book '{}': ", bookName, e);
            redirectAttributes.addFlashAttribute("error", "System error occurred");
            return "redirect:/books/all";
        }
    }
}
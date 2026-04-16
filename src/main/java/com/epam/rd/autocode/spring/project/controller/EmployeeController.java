package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
@Slf4j
public class EmployeeController {

    private final OrderService orderService;
    private final BookService bookService;

    public EmployeeController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(
            @RequestParam(defaultValue = "0") int ordersPage,
            @RequestParam(defaultValue = "0") int booksPage,
            Model model) {
        int pageSize = 8;
        Page<OrderDTO> orders = orderService.getAllOrders(ordersPage, pageSize);
        Page<BookDTO> books = bookService.getAllBooks(booksPage, pageSize, "name", "asc");
        model.addAttribute("ordersPage", orders);
        model.addAttribute("booksPage", books);
        model.addAttribute("totalOrdersCount", orders.getTotalElements());
        model.addAttribute("totalBooksCount", books.getTotalElements());
        return "employee-dashboard";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "employee-order-details";
    }
}
package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private BookService bookService;

    @BeforeEach
    void setUp() {
        when(orderService.getAllOrders(anyInt(), anyInt())).thenReturn(Page.empty());
        when(bookService.getAllBooks(anyInt(), anyInt(), anyString(), anyString())).thenReturn(Page.empty());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void shouldReturnEmployeeDashboard() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-dashboard"))
                .andExpect(model().attributeExists("ordersPage", "booksPage"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void shouldReturnOrderDetails() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(new OrderDTO());

        mockMvc.perform(get("/employee/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-order-details"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void shouldRedirectAnonymousToLogin() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().is4xxClientError());
    }
}
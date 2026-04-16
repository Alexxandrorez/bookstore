package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.UserPrincipal;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MessageSource messageSource;

    private UsernamePasswordAuthenticationToken getMockAuth(String email, String role) {
        UserPrincipal principal = new UserPrincipal(email, "pass", role, "Test Name");
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    void shouldReturnClientOrders() throws Exception {
        String email = "client@test.com";
        when(orderService.getOrdersByClient(email)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/client")
                        .with(authentication(getMockAuth(email, "CUSTOMER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("client-orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void shouldReturnEmployeeOrders() throws Exception {
        String email = "emp@test.com";
        when(orderService.getOrdersByEmployee(email)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/employee")
                        .with(authentication(getMockAuth(email, "EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-orders"));
    }

    @Test
    void shouldAddOrderSuccessfully() throws Exception {
        String email = "client@test.com";
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Success");

        mockMvc.perform(post("/orders/add")
                        .param("bookName", "JavaBook")
                        .with(csrf())
                        .with(authentication(getMockAuth(email, "CUSTOMER"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all"));
    }


    @Test
    void shouldAllowAdminToSeeEmployeeOrders() throws Exception {
        String email = "admin@test.com";
        mockMvc.perform(get("/orders/employee")
                        .with(authentication(getMockAuth(email, "ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-orders"));
    }
}
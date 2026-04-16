package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrdersByClient(String clientEmail);
    List<OrderDTO> getOrdersByEmployee(String employeeEmail);
    OrderDTO addOrder(String clientEmail, String bookName);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
    Page<OrderDTO> getAllOrders(int page, int size);
}
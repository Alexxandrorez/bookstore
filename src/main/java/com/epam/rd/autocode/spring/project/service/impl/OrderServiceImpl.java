package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InsufficientFundsException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;

    public OrderServiceImpl(OrderRepository orderRepository, BookRepository bookRepository,
                            ClientRepository clientRepository, EmployeeRepository employeeRepository,
                            ModelMapper modelMapper, MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @Override
    public Page<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(order -> modelMapper.map(order, OrderDTO.class));
    }

    @Override
    @Transactional
    public OrderDTO addOrder(String clientEmail, String bookName) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found: " + clientEmail));

        Book book = bookRepository.findByName(bookName)
                .orElseThrow(() -> new NotFoundException("Book not found: " + bookName));

        if (client.getBalance().compareTo(book.getPrice()) < 0) {
            String msg = messageSource.getMessage(
                    "error.insufficient.funds",
                    new Object[]{client.getBalance()},
                    LocaleContextHolder.getLocale()
            );
            throw new InsufficientFundsException(msg);
        }

        client.setBalance(client.getBalance().subtract(book.getPrice()));
        clientRepository.save(client);

        Order orderEntity = new Order();
        orderEntity.setClient(client);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setPrice(book.getPrice());

        BookItem bookItem = new BookItem();
        bookItem.setBook(book);
        bookItem.setQuantity(1);
        bookItem.setOrder(orderEntity);

        orderEntity.setBookItems(List.of(bookItem));

        Order savedOrder = orderRepository.save(orderEntity);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        return orderRepository.findAllByClientEmail(clientEmail).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    @Override
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        return orderRepository.findAllByEmployeeEmail(employeeEmail).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
    }
}
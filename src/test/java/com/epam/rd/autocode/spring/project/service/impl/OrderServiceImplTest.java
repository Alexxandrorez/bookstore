package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InsufficientFundsException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client client;
    private Book book;
    private final String EMAIL = "client@test.com";
    private final String BOOK_NAME = "Java Core";

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail(EMAIL);
        client.setBalance(new BigDecimal("100.00"));

        book = new Book();
        book.setName(BOOK_NAME);
        book.setPrice(new BigDecimal("60.00"));
    }

    @Test
    void addOrderShouldSaveWhenBalanceIsOk() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));
        when(bookRepository.findByName(BOOK_NAME)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderDTO result = orderService.addOrder(EMAIL, BOOK_NAME);

        assertNotNull(result);
        assertEquals(new BigDecimal("40.00"), client.getBalance());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrderShouldThrowExceptionIfBalanceIsLow() {
        book.setPrice(new BigDecimal("150.00"));
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));
        when(bookRepository.findByName(BOOK_NAME)).thenReturn(Optional.of(book));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Error");

        assertThrows(InsufficientFundsException.class, () -> orderService.addOrder(EMAIL, BOOK_NAME));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void addOrderShouldThrowExceptionIfClientNotFound() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.addOrder(EMAIL, BOOK_NAME));
    }

    @Test
    void getOrderByIdShouldReturnDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));
        when(modelMapper.map(any(), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderByIdShouldThrowExceptionIfNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(99L));
    }
}
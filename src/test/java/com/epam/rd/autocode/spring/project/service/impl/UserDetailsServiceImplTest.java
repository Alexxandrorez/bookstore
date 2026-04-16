package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private final String EMAIL = "test@example.com";
    private final String PASS = "encoded_pass";
    private final String NAME = "Oleksandr";

    @Test
    void loadUserShouldReturnClientDetails() {
        Client client = new Client();
        client.setEmail(EMAIL);
        client.setPassword(PASS);
        client.setName(NAME);
        client.setRole(Role.CUSTOMER);

        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));

        UserDetails result = userDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(NAME, result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("CUSTOMER")));
    }

    @Test
    void loadUserShouldReturnEmployeeDetails() {
        Employee employee = new Employee();
        employee.setEmail(EMAIL);
        employee.setPassword(PASS);
        employee.setName(NAME);
        employee.setRole(Role.ADMIN);

        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(employee));

        UserDetails result = userDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(NAME, result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN")));
    }

    @Test
    void loadUserShouldThrowExceptionIfUserNotFound() {
        when(clientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@test.com"));
    }
}
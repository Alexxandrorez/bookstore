package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;
    private final String EMAIL = "staff@bookstore.com";
    private final String PHONE = "555-0101";

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmail(EMAIL);
        employee.setPhone(PHONE);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail(EMAIL);
        employeeDTO.setPhone(PHONE);
        employeeDTO.setPassword("secret");
    }

    @Test
    void getEmployeeByEmailShouldReturnDto() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeByEmail(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void getEmployeeByEmailShouldThrowExceptionIfNotFound() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.getEmployeeByEmail(EMAIL));
    }

    @Test
    void addEmployeeShouldSaveAndReturnDto() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(employeeRepository.findByPhone(PHONE)).thenReturn(Optional.empty());
        when(modelMapper.map(employeeDTO, Employee.class)).thenReturn(employee);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.addEmployee(employeeDTO);

        assertNotNull(result);
        verify(passwordEncoder).encode("secret");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void addEmployeeShouldThrowExceptionIfEmailExists() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(employee));

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(employeeDTO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void addEmployeeShouldThrowExceptionIfPhoneExists() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(employeeRepository.findByPhone(PHONE)).thenReturn(Optional.of(employee));

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(employeeDTO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deleteEmployeeShouldWorkIfEmployeeExists() {
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployeeByEmail(EMAIL);

        verify(employeeRepository).delete(employee);
    }
}
package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               ModelMapper modelMapper,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .toList();
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(int page, int size) {
        return employeeRepository.findAll(PageRequest.of(page, size))
                .map(emp -> modelMapper.map(emp, EmployeeDTO.class));
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employeeDTO) {
        Employee employeeEnt = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));

        modelMapper.map(employeeDTO, employeeEnt);

        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employeeEnt.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }

        Employee savedEmployee = employeeRepository.save(employeeEnt);
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployeeByEmail(String email) {
        Employee employeeEnt = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));

        employeeRepository.delete(employeeEnt);
    }

    @Override
    @Transactional
    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new AlreadyExistException("Employee with this email already exists");
        }

        if (employeeRepository.findByPhone(employeeDTO.getPhone()).isPresent()) {
            throw new AlreadyExistException("Employee with this phone number already exists");
        }

        Employee employeeEnt = modelMapper.map(employeeDTO, Employee.class);
        employeeEnt.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));

        try {
            Employee savedEmployee = employeeRepository.save(employeeEnt);
            return modelMapper.map(savedEmployee, EmployeeDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to save employee: please check the data integrity");
        }
    }
}
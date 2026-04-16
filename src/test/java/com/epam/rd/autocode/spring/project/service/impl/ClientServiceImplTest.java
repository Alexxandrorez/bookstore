package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDto;
    private final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail(EMAIL);

        clientDto = new ClientDTO();
        clientDto.setEmail(EMAIL);
        clientDto.setPassword("rawPassword");
    }

    @Test
    void getClientByEmailShouldReturnDto() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDto);

        ClientDTO result = clientService.getClientByEmail(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void getClientByEmailShouldThrowExceptionIfNotFound() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.getClientByEmail(EMAIL));
    }

    @Test
    void addClientShouldSaveAndReturnDto() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(modelMapper.map(clientDto, Client.class)).thenReturn(client);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDto);

        ClientDTO result = clientService.addClient(clientDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("rawPassword");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void addClientShouldThrowExceptionIfEmailExistsInClients() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(clientDto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void addClientShouldThrowExceptionIfEmailExistsInEmployees() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Employee()));

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(clientDto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void deleteClientShouldWorkIfClientExists() {
        when(clientRepository.findByEmail(EMAIL)).thenReturn(Optional.of(client));

        clientService.deleteClientByEmail(EMAIL);

        verify(clientRepository).delete(client);
    }
}
package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository,
                             EmployeeRepository employeeRepository,
                             ModelMapper modelMapper,
                             BCryptPasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();
    }

    public Page<ClientDTO> getAllClients(int page, int size) {
        return clientRepository.findAll(PageRequest.of(page, size))
                .map(client -> modelMapper.map(client, ClientDTO.class));
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        Client clientEnt = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));
        return modelMapper.map(clientEnt, ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO updateClientByEmail(String email, ClientDTO clientDto) {
        Client clientEnt = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        modelMapper.map(clientDto, clientEnt);

        if (clientDto.getPassword() != null && !clientDto.getPassword().isBlank()) {
            clientEnt.setPassword(passwordEncoder.encode(clientDto.getPassword()));
        }

        Client clientSaved = clientRepository.save(clientEnt);
        return modelMapper.map(clientSaved, ClientDTO.class);
    }

    @Override
    @Transactional
    public void deleteClientByEmail(String email) {
        Client clientEnt = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        clientRepository.delete(clientEnt);
    }

    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO clientDto) {
        if (clientRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("This email already exists");
        }
        if (employeeRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("This email already exists");
        }

        Client clientEnt = modelMapper.map(clientDto, Client.class);

        if (clientDto.getPassword() != null && !clientDto.getPassword().isBlank()) {
            clientEnt.setPassword(passwordEncoder.encode(clientDto.getPassword()));
        }

        if (clientEnt.getBalance() == null) {
            clientEnt.setBalance(BigDecimal.valueOf(1000.00));
        }

        clientEnt.setRole(Role.CUSTOMER);

        Client savedClient = clientRepository.save(clientEnt);

        return modelMapper.map(savedClient, ClientDTO.class);
    }


}
package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getAllClients();
    ClientDTO getClientByEmail(String email);
    ClientDTO updateClientByEmail(String email, ClientDTO client);
    void deleteClientByEmail(String email);
    ClientDTO addClient(ClientDTO client);
    Page<ClientDTO> getAllClients(int page, int size);
}

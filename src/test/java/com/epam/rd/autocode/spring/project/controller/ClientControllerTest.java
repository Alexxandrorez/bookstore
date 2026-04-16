package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListAllClientsForAdmin() throws Exception {
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/clients/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/clients-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnClientProfileForAdmin() throws Exception {
        ClientDTO client = new ClientDTO();
        client.setEmail("test@test.com");
        client.setName("Test Client");

        when(clientService.getClientByEmail(anyString())).thenReturn(client);

        mockMvc.perform(get("/clients/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/profile"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteClientByAdmin() throws Exception {
        mockMvc.perform(post("/clients/delete")
                        .param("email", "to-delete@test.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/all"));
    }

}
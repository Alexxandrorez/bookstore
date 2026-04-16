package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private ClientService clientService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnDashboard() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowClientProfile() throws Exception {
        ClientDTO client = new ClientDTO();
        client.setEmail("test@test.com");
        client.setName("Test Client");

        when(clientService.getClientByEmail(anyString())).thenReturn(client);

        mockMvc.perform(get("/admin/clients/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/profile"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAddEmployeeSuccessfully() throws Exception {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setEmail("new@test.com");
        employee.setName("Ivan Ivanov");
        employee.setPhone("0501234567");
        employee.setPassword("password123");
        employee.setBirthDate(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/admin/employees/add")
                        .flashAttr("employee", employee)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/employees"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@test.com")
    void shouldDeleteOtherClient() throws Exception {
        mockMvc.perform(post("/admin/clients/delete")
                        .param("email", "other@test.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/clients/all"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListEmployees() throws Exception {
        when(employeeService.getAllEmployees(anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/admin/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employees-list"));
    }
}
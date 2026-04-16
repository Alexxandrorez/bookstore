package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String listAllClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "admin/clients-list";
    }

    @GetMapping("/{email:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public String clientProfile(@PathVariable String email, Model model) {
        model.addAttribute("client", clientService.getClientByEmail(email));
        return "admin/profile";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteClient(@RequestParam String email) {
        log.info("Deleting client with email: {}", email);
        try {
            clientService.deleteClientByEmail(email);
            return "redirect:/clients/all";
        } catch (Exception e) {
            log.error("Error deleting client {}: ", email, e);
            return "redirect:/clients/all";
        }
    }
}
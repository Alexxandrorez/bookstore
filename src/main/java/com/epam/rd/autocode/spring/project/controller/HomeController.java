package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final ClientService clientService;

    public HomeController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new ClientDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("client") ClientDTO clientDTO,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "register";
        }
        try {
            clientService.addClient(clientDTO);
            return "redirect:/login?success";
        } catch (AlreadyExistException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
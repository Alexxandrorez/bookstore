package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final ClientService clientService;

    public AdminController(EmployeeService employeeService, ClientService clientService) {
        this.employeeService = employeeService;
        this.clientService = clientService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("employeeCount", employeeService.getAllEmployees().size());
        model.addAttribute("clientCount", clientService.getAllClients().size());
        return "admin/dashboard";
    }

    @GetMapping("/clients/all")
    public String listAllClients(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 8;
        Page<ClientDTO> clientPage = clientService.getAllClients(page, pageSize);
        model.addAttribute("clients", clientPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientPage.getTotalPages());
        return "admin/clients-list";
    }

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 8;
        Page<EmployeeDTO> employeePage = employeeService.getAllEmployees(page, pageSize);
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        return "admin/employees-list";
    }

    @GetMapping("/clients/{email:.+}")
    public String clientProfile(@PathVariable String email, Model model) {
        model.addAttribute("client", clientService.getClientByEmail(email));
        return "admin/profile";
    }

    @PostMapping("/clients/delete")
    @PreAuthorize("hasRole('ADMIN') and #email != authentication.name")
    public String deleteClient(@RequestParam String email) {
        log.info("Deleting client: {}", email);
        clientService.deleteClientByEmail(email);
        return "redirect:/admin/clients/all";
    }

    @PostMapping("/employees/delete")
    @PreAuthorize("hasRole('ADMIN') and #email != authentication.name")
    public String deleteEmployee(@RequestParam String email) {
        log.info("Deleting employee: {}", email);
        employeeService.deleteEmployeeByEmail(email);
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/new")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "admin/employee-form";
    }

    @PostMapping("/employees/add")
    public String addEmployee(@Valid @ModelAttribute("employee") EmployeeDTO employeeDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/employee-form";
        }
        try {
            employeeService.addEmployee(employeeDTO);
            log.info("Successfully added employee: {}", employeeDTO.getEmail());
            return "redirect:/admin/employees";
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to add employee {} - duplicate data", employeeDTO.getEmail());
            model.addAttribute("errorMessage", "An employee with this phone number or email address already exists.");
            return "admin/employee-form";
        } catch (Exception e) {
            log.error("Unexpected error adding employee: ", e);
            model.addAttribute("errorMessage", "A system error has occurred.");
            return "admin/employee-form";
        }
    }
}
package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.UserPrincipal;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(ClientRepository clientRepo, EmployeeRepository employeeRepo) {
        this.clientRepository = clientRepo;
        this.employeeRepository = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            var c = client.get();
            Role role = c.getRole() != null ? c.getRole() : Role.CUSTOMER;
            return new UserPrincipal(c.getEmail(), c.getPassword(), role.name(), c.getName());
        }

        return employeeRepository.findByEmail(email)
                .map(e -> {
                    String role = e.getRole().toString();
                    return new UserPrincipal(e.getEmail(), e.getPassword(), role, e.getName());
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
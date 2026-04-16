package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends User {

    @NotBlank
    @Column(unique = true)
    private String phone;

    @NotNull
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    public Employee(Long id, String name, String password, String email, String phone, LocalDate birthDate, Role role) {
        super(id, name, password, email);
        this.phone = phone;
        this.birthDate = birthDate;
        this.role = role;
    }
}
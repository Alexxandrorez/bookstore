package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @NotBlank(message = "{validation.employeeDTO.email.notblank}")
    @Email(message = "{validation.employeeDTO.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.employeeDTO.password.notblank}")
    @Size(min = 8, max = 64, message = "{validation.employeeDTO.password.size}")
    private String password;

    @NotBlank(message = "{validation.employeeDTO.name.notblank}")
    private String name;

    @NotBlank(message = "{validation.employeeDTO.phone.notblank}")
    @Pattern(regexp = "^\\d{10}$", message = "{validation.employeeDTO.phone.pattern}")
    private String phone;

    private Role role;

    @NotNull(message = "{validation.employeeDTO.birthdate.notnull}")
    private LocalDate birthDate;
}
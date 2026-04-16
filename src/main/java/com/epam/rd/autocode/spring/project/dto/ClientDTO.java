package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO{

    @NotBlank(message = "{validation.clientDTO.email.notblank}")
    @Email(message = "{validation.clientDTO.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.clientDTO.password.notblank}")
    @Size(min = 8, max = 64, message = "{validation.clientDTO.password.size}")
    private String password;

    @NotBlank(message = "{validation.clientDTO.name.notblank}")
    private String name;

    @DecimalMin(value = "0.0", message = "{validation.clientDTO.balance.min}")
    private BigDecimal balance;

}

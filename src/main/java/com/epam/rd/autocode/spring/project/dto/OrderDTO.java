package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @Email
    @NotBlank
    private String clientEmail;

    @Email
    private String employeeEmail;

    private LocalDateTime orderDate;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotEmpty
    private List<BookItemDTO> bookItems;
}
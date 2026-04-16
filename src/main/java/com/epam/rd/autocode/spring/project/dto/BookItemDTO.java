package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemDTO {

    @NotBlank
    private String bookName;

    @NotNull
    @Min(1)
    private Integer quantity;
}
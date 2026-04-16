package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    @NotBlank
    private String name;

    private String genre;

    private AgeGroup ageGroup;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    private LocalDate publicationDate;

    @NotBlank
    private String author;

    private Integer pages;

    private String characteristics;

    private String description;

    private Language language;
}
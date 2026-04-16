package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String genre;

    @Column(name = "age_group")
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @NotBlank
    private String author;

    private Integer pages;

    private String characteristics;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Language language;

}

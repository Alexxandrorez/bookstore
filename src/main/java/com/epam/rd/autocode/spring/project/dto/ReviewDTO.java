package com.epam.rd.autocode.spring.project.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private String clientName;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;
}
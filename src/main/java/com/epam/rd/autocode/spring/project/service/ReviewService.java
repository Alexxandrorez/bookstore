package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    void addReview(String bookName, String clientName, int rating, String comment);
    List<ReviewDTO> getReviewsByBook(String bookName);
}
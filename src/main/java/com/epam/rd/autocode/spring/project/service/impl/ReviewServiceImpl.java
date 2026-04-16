package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ReviewDTO;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Review;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.ReviewRepository;
import com.epam.rd.autocode.spring.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             BookRepository bookRepository,
                             ClientRepository clientRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public void addReview(String bookName, String clientName, int rating, String comment) {
        Book book = bookRepository.findByName(bookName)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Client client = clientRepository.findByName(clientName)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Review review = new Review();
        review.setBook(book);
        review.setClient(client);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
    }

    @Override
    @Transactional()
    public List<ReviewDTO> getReviewsByBook(String bookName) {
        return reviewRepository.findByBookName(bookName).stream()
                .map(r -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setClientName(r.getClient().getName());
                    dto.setRating(r.getRating());
                    dto.setComment(r.getComment());
                    dto.setCreatedAt(r.getCreatedAt());
                    return dto;
                }).collect(Collectors.toList());
    }
}
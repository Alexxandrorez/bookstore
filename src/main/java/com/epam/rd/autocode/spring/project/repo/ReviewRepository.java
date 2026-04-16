package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookName(String bookName);
}
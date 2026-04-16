package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add/{bookName}")
    public String addReview(@PathVariable String bookName,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal) {
        reviewService.addReview(bookName, principal.getName(), rating, comment);
        return "redirect:/books/details/" + bookName;
    }
}
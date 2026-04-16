package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.WishlistService;
import com.epam.rd.autocode.spring.project.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/books")
@Slf4j
public class BookController {

    private final BookService bookService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;

    public BookController(BookService bookService, WishlistService wishlistService, ReviewService reviewService) {
        this.bookService = bookService;
        this.wishlistService = wishlistService;
        this.reviewService = reviewService;
    }

    @GetMapping("/all")
    public String getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Principal principal,
            HttpServletRequest request,
            Model model) {

        int pageSize = 12;
        Page<BookDTO> bookPage = bookService.getAllBooks(page, pageSize, sortBy, direction);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        if (principal != null && request.isUserInRole("ROLE_CUSTOMER")) {
            List<String> wishlistNames = wishlistService.getWishlist(principal.getName())
                    .stream().map(BookDTO::getName).toList();
            model.addAttribute("wishlistBookNames", wishlistNames);
        }

        return "books-list";
    }

    @GetMapping("/details/{name}")
    public String getBookByName(@PathVariable String name, Model model){
        model.addAttribute("book", bookService.getBookByName(name));
        model.addAttribute("reviews", reviewService.getReviewsByBook(name));
        return "book-details";
    }

    @GetMapping("/edit/{name}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String showEditForm(@PathVariable String name, Model model) {
        BookDTO book = bookService.getBookByName(name);
        model.addAttribute("book", book);
        model.addAttribute("oldName", name);
        return "edit-book";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String updateBook(@RequestParam("oldName") String oldName,
                             @Valid @ModelAttribute("book") BookDTO bookDTO,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("oldName", oldName);
            return "edit-book";
        }
        try {
            bookService.updateBookByName(oldName, bookDTO);
            log.info("Book updated: {} (old name: {})", bookDTO.getName(), oldName);
            return "redirect:/employee/dashboard";
        } catch (Exception e) {
            log.error("Error updating book {}: ", oldName, e);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("oldName", oldName);
            return "edit-book";
        }
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String deleteBookByName(@RequestParam String name, RedirectAttributes redirectAttributes){
        try {
            log.info("Deleting book: {}", name);
            bookService.deleteBookByName(name);
            redirectAttributes.addFlashAttribute("message", "Book successfully deleted");
        } catch (Exception e) {
            log.error("Failed to delete book: {}", name, e);
            redirectAttributes.addFlashAttribute("error", "The book could not be deleted: it is already listed in the orders");
        }
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "add-book";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String addBook(@Valid @ModelAttribute("book") BookDTO bookDTO,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "add-book";
        }
        try {
            bookService.addBook(bookDTO);
            log.info("New book added: {}", bookDTO.getName());
            return "redirect:/employee/dashboard";
        } catch (Exception e) {
            log.error("Failed to add book {}: ", bookDTO.getName(), e);
            model.addAttribute("errorMessage", "Failed to add book: " + e.getMessage());
            return "add-book";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "name") String sortBy,
                         @RequestParam(defaultValue = "asc") String direction,
                         Principal principal,
                         HttpServletRequest request,
                         Model model) {
        Page<BookDTO> bookPage = bookService.searchBooks(query, page, 10);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        if (principal != null && request.isUserInRole("ROLE_CUSTOMER")) {
            List<String> wishlistNames = wishlistService.getWishlist(principal.getName())
                    .stream().map(BookDTO::getName).toList();
            model.addAttribute("wishlistBookNames", wishlistNames);
        }

        return "books-list";
    }
}
package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/toggle/{name}")
    public String toggleWishlist(@PathVariable String name, Principal principal, HttpServletRequest request) {
        wishlistService.toggleWishlist(principal.getName(), name);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/books/all");
    }

    @GetMapping
    public String showWishlist(Principal principal, Model model) {
        List<BookDTO> wishlist = wishlistService.getWishlist(principal.getName());
        model.addAttribute("books", wishlist);
        model.addAttribute("isWishlistPage", true);

        List<String> wishlistNames = wishlist.stream().map(BookDTO::getName).toList();
        model.addAttribute("wishlistBookNames", wishlistNames);

        return "books-list";
    }
}
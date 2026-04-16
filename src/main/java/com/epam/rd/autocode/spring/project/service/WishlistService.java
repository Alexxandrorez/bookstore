package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import java.util.List;

public interface WishlistService {
    void toggleWishlist(String email, String bookName);
    List<BookDTO> getWishlist(String email);
}
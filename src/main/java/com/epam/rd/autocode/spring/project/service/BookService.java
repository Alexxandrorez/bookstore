package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    List<BookDTO> getAllBooks();
    BookDTO getBookByName(String name);
    BookDTO updateBookByName(String name, BookDTO book);
    void deleteBookByName(String name);
    BookDTO addBook(BookDTO book);
    Page<BookDTO> getAllBooks(int page, int size);
    Page<BookDTO> getAllBooks(int page, int size, String sortBy, String direction);
    Page<BookDTO> searchBooks(String query, int page, int size);
}
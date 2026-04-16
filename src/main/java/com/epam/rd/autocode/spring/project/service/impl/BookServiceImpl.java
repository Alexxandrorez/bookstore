package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<BookDTO> getAllBooks(int page, int size) {
        return getAllBooks(page, size, "name", "asc");
    }

    public Page<BookDTO> getAllBooks(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(book -> modelMapper.map(book, BookDTO.class));
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .toList();
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO book) {
        Book bookEnt = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Not found"));
        modelMapper.map(book, bookEnt);

        Book savedBook = bookRepository.save(bookEnt);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public void deleteBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Not found"));
        bookRepository.delete(book);
    }

    @Override
    public BookDTO addBook(BookDTO book) {
        Book bookEnt = modelMapper.map(book, Book.class);
        bookRepository.save(bookEnt);
        return modelMapper.map(bookEnt, BookDTO.class);
    }

    @Override
    public BookDTO getBookByName(String name) {
        return bookRepository.findByName(name)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(()->new NotFoundException("Not found"));
    }
    @Override
    public Page<BookDTO> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return bookRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(book -> modelMapper.map(book, BookDTO.class));
    }
}

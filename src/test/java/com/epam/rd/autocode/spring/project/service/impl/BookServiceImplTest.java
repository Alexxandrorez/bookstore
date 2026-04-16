package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;
    private final String BOOK_NAME = "Clean Code";

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setName(BOOK_NAME);

        bookDTO = new BookDTO();
        bookDTO.setName(BOOK_NAME);
    }

    @Test
    void shouldReturnBookDtoWhenBookExistsByName() {
        when(bookRepository.findByName(BOOK_NAME)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookByName(BOOK_NAME);

        assertNotNull(result);
        assertEquals(BOOK_NAME, result.getName());
        verify(bookRepository).findByName(BOOK_NAME);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookDoesNotExistByName() {
        when(bookRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookByName("Unknown"));
    }

    @Test
    void shouldSaveAndReturnBookDtoWhenAddingNewBook() {
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
        verify(modelMapper).map(bookDTO, Book.class);
    }

    @Test
    void shouldDeleteBookWhenBookExistsByName() {
        when(bookRepository.findByName(BOOK_NAME)).thenReturn(Optional.of(book));

        bookService.deleteBookByName(BOOK_NAME);

        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentBook() {
        when(bookRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBookByName("Unknown"));
    }

    @Test
    void shouldReturnPageOfBookDtosWhenAllBooksRequestedWithPagination() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getAllBooks(0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BOOK_NAME, result.getContent().get(0).getName());
        verify(bookRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnListOfAllBookDtos() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BOOK_NAME, result.get(0).getName());
        verify(bookRepository).findAll();
    }


}
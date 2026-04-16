package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.WishlistService;
import com.epam.rd.autocode.spring.project.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private ReviewService reviewService;

    private BookDTO validBook;

    @BeforeEach
    void setUp() {
        validBook = new BookDTO();
        validBook.setName("Test Book");
        validBook.setAuthor("Test Author");
        validBook.setPrice(new BigDecimal("100.00"));
        validBook.setDescription("Description");

        when(bookService.getBookByName(anyString())).thenReturn(validBook);
        when(bookService.getAllBooks(anyInt(), anyInt(), anyString(), anyString())).thenReturn(Page.empty());
        when(reviewService.getReviewsByBook(anyString())).thenReturn(Collections.emptyList());
    }

    @Test
    @WithMockUser
    void shouldReturnAllBooks() throws Exception {
        mockMvc.perform(get("/books/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("books-list"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser
    void shouldReturnBookDetails() throws Exception {
        mockMvc.perform(get("/books/details/Java"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void shouldShowEditFormForEmployee() throws Exception {
        mockMvc.perform(get("/books/edit/Java"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-book"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateBookByAdmin() throws Exception {
        mockMvc.perform(post("/books/update")
                        .param("oldName", "OldName")
                        .flashAttr("book", validBook)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/dashboard"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void shouldDeleteBookByEmployee() throws Exception {
        mockMvc.perform(post("/books/delete")
                        .param("name", "Java")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddFormForAdmin() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void shouldAddBookByEmployee() throws Exception {
        mockMvc.perform(post("/books/add")
                        .flashAttr("book", validBook)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/dashboard"));
    }
}
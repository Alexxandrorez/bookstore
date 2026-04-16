package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public WishlistServiceImpl(ClientRepository clientRepository,
                               BookRepository bookRepository,
                               ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void toggleWishlist(String name, String bookName) {
        Client client = clientRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Client not found: " + name));
        Book book = bookRepository.findByName(bookName)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookName));

        if (client.getWishlist().contains(book)) {
            client.getWishlist().remove(book);
        } else {
            client.getWishlist().add(book);
        }
        clientRepository.save(client);
    }


    @Override
    @Transactional()
    public List<BookDTO> getWishlist(String name) {
        Client client = clientRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Client not found: " + name));
        return client.getWishlist().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }
}
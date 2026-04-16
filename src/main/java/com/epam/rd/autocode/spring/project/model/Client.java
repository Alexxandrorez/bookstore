package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "clients")
public class Client extends User {

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.CUSTOMER;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_wishlist",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> wishlist = new HashSet<>();

    public Client(Long id, String name, String password, String email, BigDecimal balance) {
        super(id, name, password, email);
        this.balance = balance;
        this.role = Role.CUSTOMER;
    }
}
package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Book> books; // Add this field to establish the relationship
}

package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 100, message = "Tên sách phải ít hơn 100 ký tự")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả phải ít hơn 100 ký tự")
    @Column(name = "author")
    private String author;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 100, message = "Mô tả phải ít hơn 100 ký tự")
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BorrowRecord> borrowRecords;
    private String ImagePath;
}

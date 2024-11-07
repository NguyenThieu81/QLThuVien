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

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author; // Change from String to Author object

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 5000, message = "Mô tả phải ít hơn 5000 ký tự")
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    private String imagePath; // Changed to camelCase


    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    @Column(name = "quantity")
    private Integer quantity;
    public void borrow() {
        if (this.quantity > 0) {
            this.quantity--;
        } else {
            throw new IllegalStateException("Không còn sách để mượn.");
        }
    }

    public void returnBook() {
        this.quantity++;
    }
}
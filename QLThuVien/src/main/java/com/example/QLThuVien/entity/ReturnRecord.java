package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "return_record")
public class ReturnRecord extends TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người trả sách

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Sách được trả

    private LocalDateTime returnDate; // Ngày giờ trả sách
    public String getFormattedReturnDate() {
        if (this.returnDate != null) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.returnDate);
        } else {
            return "Ngày trả không xác định";
        }
    }
}
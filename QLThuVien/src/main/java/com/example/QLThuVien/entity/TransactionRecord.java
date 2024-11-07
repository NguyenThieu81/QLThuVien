package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@MappedSuperclass
public abstract class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người thực hiện giao dịch

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Sách liên quan

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate; // Ngày giờ giao dịch
    public String getFormattedTransactionDate() {
        if (this.transactionDate != null) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.transactionDate);
        } else {
            return "Ngày trả không xác định";
        }
    }
}
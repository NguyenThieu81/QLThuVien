package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "borrow_record")
public class BorrowRecord extends TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người mượn

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Sách được mượn

    private LocalDateTime borrowDate; // Ngày giờ mượn
    @Column(name = "transaction_date", nullable = false) // Đảm bảo trường này không thể null
    private LocalDateTime transactionDate;
    @Column(name = "is_returned")
    private boolean isReturned = false; // Mặc định là chưa trả

    // Các phương thức getter và setter
    public boolean isReturned() {
        return isReturned;
    }
    public void setIsReturned(boolean isReturned) {
        this.isReturned = isReturned;
                }
    public String getFormattedBorrowDate() {
        if (this.borrowDate != null) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.borrowDate);
        } else {
            return "Ngày mượn không xác định";
        }
    }

}


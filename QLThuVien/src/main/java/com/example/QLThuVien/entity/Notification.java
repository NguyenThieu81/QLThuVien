package com.example.QLThuVien.entity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    private String message;

    private LocalDateTime createdAt;
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt != null ? createdAt.format(formatter) : null;
    }
    private boolean isRead;
    // Thêm trường type
    private String type; // "APPOINTMENT_CONFIRMATION", "BOOK_BORROW", "BOOK_RETURN"

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    public void markAsRead()
    { this.isRead = true;}
}

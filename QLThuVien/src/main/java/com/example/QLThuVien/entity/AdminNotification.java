package com.example.QLThuVien.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_notification")
public class AdminNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String searchKeyword;
    private String message;

    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = true) // Có thể null nếu không liên quan đến cuộc hẹn
    private Appointment appointment;
    // Phương thức để đánh dấu thông báo là đã đọc
    public void markAsRead() {
        this.isRead = true;
    }
    public String getSearchKeyword() {
        return searchKeyword;
    }
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
    // Getter cho id
    public Long getId() {
        return id;
    }
    // Getter cho message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}

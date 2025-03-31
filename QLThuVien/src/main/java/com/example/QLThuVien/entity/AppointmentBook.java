package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@Entity
@Table(name = "appointment_books")
@EqualsAndHashCode(exclude = "appointment")
public class AppointmentBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private long cost; // Tổng tiền cho cuốn sách


    @Override
    public int hashCode() {
        return Objects.hash(id, book, appointment); // Chỉ sử dụng các thuộc tính không tham chiếu đến Appointment
    }
}
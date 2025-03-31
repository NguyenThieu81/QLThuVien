package com.example.QLThuVien.repository;

import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByUser (User user);

    @Query("SELECT a FROM Appointment a JOIN a.appointmentBooks ab")
    Page<Appointment> findAllWithBooks(Pageable pageable);

    @Query("SELECT SUM(a.totalCost) FROM Appointment a")
    Long getTotalRevenue();

    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Page<Appointment> findById(@Param("id") Long id, Pageable pageable);

    // Lấy tất cả các cuộc hẹn đang mượn
    @Query("SELECT a FROM Appointment a WHERE a.status = 'COMPLETED'")
    List<Appointment> findAllWithBorrowedAppointments();

    // Lấy tất cả các cuộc hẹn đã trả
    @Query("SELECT a FROM Appointment a WHERE a.status = 'RETURNED'")
    List<Appointment> findAllWithReturnedAppointments();

    @Query("SELECT a FROM Appointment a WHERE a.user.username LIKE %:username%")
    Page<Appointment> findByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.user.username LIKE %:username% AND a.status = 'COMPLETED'")
    List<Appointment> findBorrowedAppointmentsByUsername(@Param("username") String username);

    @Query("SELECT a FROM Appointment a WHERE a.user.username LIKE %:username% AND a.status = 'RETURNED'")
    List<Appointment> findReturnedAppointmentsByUsername(@Param("username") String username);

    @Query("SELECT ab.book.title AS bookTitle, COUNT(ab) AS count " +
            "FROM AppointmentBook ab " +
            "GROUP BY ab.book.title " +
            "ORDER BY count DESC")
    List<Object[]> findMostBorrowedBooks();
    List<Appointment> findByStatusIn(AppointmentStatus... statuses);
}


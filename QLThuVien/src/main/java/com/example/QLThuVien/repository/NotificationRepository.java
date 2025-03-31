package com.example.QLThuVien.repository;

import com.example.QLThuVien.entity.Notification;
import com.example.QLThuVien.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countUnreadNotificationsByUser (@Param("user") User user);
    List<Notification> findByUser (User user); // Phương thức để tìm thông báo theo người dùng
    List<Notification> findByType(String type);

}

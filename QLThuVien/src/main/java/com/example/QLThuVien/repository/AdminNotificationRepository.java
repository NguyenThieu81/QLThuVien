package com.example.QLThuVien.repository;
import com.example.QLThuVien.entity.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    long countByIsReadFalse(); // Đếm số thông báo chưa đọc
    List<AdminNotification> findAll(); // Lấy tất cả thông báo

}

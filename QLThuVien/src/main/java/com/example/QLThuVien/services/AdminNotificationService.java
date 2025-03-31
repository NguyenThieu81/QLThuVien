package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.AdminNotification;
import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.repository.AdminNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminNotificationService {
    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    public AdminNotification getNotificationById(Long id) {
        return adminNotificationRepository.findById(id).orElse(null);
    }

    public void markAsRead(Long id) {
        AdminNotification notification = adminNotificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            adminNotificationRepository.save(notification);
        }
    }

    public List<AdminNotification> getAllAdminNotifications() {
        return adminNotificationRepository.findAll();
    }

    public long countUnreadAdminNotifications() {
        return adminNotificationRepository.countByIsReadFalse();
    }
    public void sendAdminNotification(Appointment appointment, String message) {
        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setMessage(message);
        adminNotification.setRead(false); // Đánh dấu là chưa đọc
        adminNotification.setAppointment(appointment); // Thiết lập cuộc hẹn liên quan
        adminNotification.setSearchKeyword(appointment.getUser().getUsername());
        adminNotificationRepository.save(adminNotification);
    }
    public void markAllAdminNotificationsAsRead() {
        List<AdminNotification> notifications = adminNotificationRepository.findAll();
        for (AdminNotification notification : notifications) {
            notification.setRead(true);
        }
        adminNotificationRepository.saveAll(notifications); // Lưu tất cả thông báo đã được cập nhật
    }
}


package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.*;

import com.example.QLThuVien.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserService userService;

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            return notificationRepository.findByUser(currentUser);
        }
        return List.of();
    }

    public void markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public long countUnreadNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            return notificationRepository.countUnreadNotificationsByUser(currentUser);
        }
        return 0;
    }

    @Transactional
    public void markAllNotificationsAsReadForCurrentUser () {
        User currentUser  = userService.getCurrentUser ();
        if (currentUser  != null) {
            List<Notification> notifications = notificationRepository.findByUser (currentUser );
            for (Notification notification : notifications) {
                notification.setRead(true); // Đánh dấu thông báo là đã đọc
                notificationRepository.save(notification); // Lưu từng thông báo
            }
        }
    }
}






package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.AdminNotification;
import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.entity.Notification;
import com.example.QLThuVien.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RevenueService revenueService;
    @Autowired private AdminNotificationService adminNotificationService;

    // Trang dashboard admin
    @GetMapping
    public String adminDashboard(Model model) {
        long totalBooks = bookService.countTotalBooks(); // Lấy tổng số sách
        model.addAttribute("totalBooks", totalBooks); // Truyền tổng số sách vào model
        Long totalRevenue = revenueService.getTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue);
        List<AdminNotification> adminNotifications = adminNotificationService.getAllAdminNotifications();
        long unreadAdminNotificationCount = adminNotificationService.countUnreadAdminNotifications();
        model.addAttribute("adminNotifications", adminNotifications); // Đảm bảo tên này khớp với tên trong HTML
        model.addAttribute("unreadAdminNotificationCount", unreadAdminNotificationCount); // Thêm biến này vào mô hình
        model.addAttribute("message", "Chào mừng đến với trang quản lý admin!");
        return "admin/admin-home"; // Đường dẫn đến tệp HTML
    }








        @GetMapping("/notification/{id}")
        public RedirectView handleAdminNotificationClick(@PathVariable Long id) {
            AdminNotification notification = adminNotificationService.getNotificationById(id);

            if (notification != null) {
                adminNotificationService.markAsRead(id);
                String searchKeyword = notification.getSearchKeyword();

                try {
                    searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return new RedirectView("/admin/appointments?searchUsername=" + searchKeyword);
            }

            return new RedirectView("/admin");
        }

    @PostMapping("/notifications/mark-all-read")
    public String markAllAdminNotificationsAsRead() {
        adminNotificationService.markAllAdminNotificationsAsRead(); // Phương thức này sẽ đánh dấu tất cả thông báo là đã đọc
        return "redirect:/admin"; // Chuyển hướng về trang admin sau khi đánh dấu
    }




}

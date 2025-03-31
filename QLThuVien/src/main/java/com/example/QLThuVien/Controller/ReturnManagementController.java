package com.example.QLThuVien.Controller;

import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.AdminNotification;
import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.services.AdminNotificationService;
import com.example.QLThuVien.services.AppointmentService;
import com.example.QLThuVien.services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/returns")
public class ReturnManagementController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired private AdminNotificationService adminNotificationService;
    // Hiển thị danh sách các cuộc hẹn đang mượn và đã trả
    @GetMapping
    public String listReturns(Model model,
                              @RequestParam(required = false) String searchBorrowedUsername,
                              @RequestParam(required = false) String searchReturnedUsername) {
        List<Appointment> borrowedAppointments;
        List<Appointment> returnedAppointments;

        if (searchBorrowedUsername != null && !searchBorrowedUsername.isEmpty()) {
            borrowedAppointments = appointmentService.getBorrowedAppointmentsByUsername(searchBorrowedUsername);
        } else {
            borrowedAppointments = appointmentService.getAllBorrowedAppointments();
        }

        if (searchReturnedUsername != null && !searchReturnedUsername.isEmpty()) {
            returnedAppointments = appointmentService.getReturnedAppointmentsByUsername(searchReturnedUsername);
        } else {
            returnedAppointments = appointmentService.getAllReturnedAppointments();
        }
        List<AdminNotification> adminNotifications = adminNotificationService.getAllAdminNotifications();
        long unreadAdminNotificationCount = adminNotificationService.countUnreadAdminNotifications();
        model.addAttribute("adminNotifications", adminNotifications); // Đảm bảo tên này khớp với tên trong HTML // Đảm bảo tên này khớp với tên trong HTML
        model.addAttribute("unreadAdminNotificationCount", unreadAdminNotificationCount); // Thêm biến này vào mô hình
        model.addAttribute("borrowedAppointments", borrowedAppointments);
        model.addAttribute("returnedAppointments", returnedAppointments);
        return "admin/borrow_management"; // Trả về view tương ứng
    }

    // Xác nhận trả sách
    @PostMapping("/confirmReturn/{id}")
    public String confirmReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Xác nhận trả sách
            Appointment appointment = appointmentService.confirmReturn(id);

            // Không cần gọi save ở đây vì confirmReturn đã lưu lại
            redirectAttributes.addFlashAttribute("message", "Trả sách thành công!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin mượn sách.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/returns"; // Chuyển hướng về trang quản lý trả sách
    }
}
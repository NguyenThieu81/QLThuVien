package com.example.QLThuVien.Controller;
import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.AdminNotification;
import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private BookService bookService;
    @Autowired private AdminNotificationService adminNotificationService;
    // Trang danh sách lịch hẹn

    @GetMapping
    public String listAppointments(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "100") int size,
                                   @RequestParam(required = false) String searchUsername) {
        Page<Appointment> appointments;
        if (searchUsername != null && !searchUsername.isEmpty()) {
            appointments = appointmentService.findAppointmentsByUsername(searchUsername, PageRequest.of(page, size));
        } else {
            appointments = appointmentService.getAllAppointments(PageRequest.of(page, size));
        }
        List<AdminNotification> adminNotifications = adminNotificationService.getAllAdminNotifications();
        long unreadAdminNotificationCount = adminNotificationService.countUnreadAdminNotifications();
        model.addAttribute("adminNotifications", adminNotifications); // Đảm bảo tên này khớp với tên trong HTML
        model.addAttribute("unreadAdminNotificationCount", unreadAdminNotificationCount); // Thêm biến này vào mô hình
        model.addAttribute("appointments", appointments.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointments.getTotalPages());

        // Lấy danh sách sách và thêm vào mô hình
        List<Book> books = bookService.getAllBooks(); // Giả sử bạn có phương thức này trong BookService
        model.addAttribute("books", books);

        return "admin/appointments_management";
    }


    // Xác nhận lịch hẹn
    @PostMapping("/confirm/{id}")
    public String confirmAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Xác nhận lịch hẹn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xác nhận lịch hẹn: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/confirmBorrow/{id}")
    public String confirmBorrow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmBorrow(id);
            redirectAttributes.addFlashAttribute("message", "Xác nhận mượn sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xác nhận mượn sách: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Hủy lịch hẹn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy lịch hẹn: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }


}

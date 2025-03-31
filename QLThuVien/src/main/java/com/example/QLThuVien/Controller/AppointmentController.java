package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.Notification;
import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.services.AppointmentService;
import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.NotificationService;
import com.example.QLThuVien.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final BookService bookService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String showAppointmentsPage(Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);
        model.addAttribute("books", bookService.getAllBooks(PageRequest.of(0, 100)).getContent());
        return "home/appointments"; // Return to the appointments.html page
    }

    @PostMapping("/create")
    public String createAppointment(@RequestParam Set<Long> bookIds,
                                    @RequestParam LocalDateTime borrowDate,
                                    @RequestParam LocalDateTime returnDate,
                                    @RequestParam(required = false) String notes,
                                    RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser ();
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đặt lịch hẹn.");
            // Ghi log thông báo lỗi
            System.out.println("Error: Bạn cần đăng nhập để đặt lịch hẹn.");
            return "redirect:/appointments"; // Redirect back to the appointments page
        }

        Set<Book> books = new HashSet<>(bookService.findAllByIds(bookIds));
        if (books.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sách nào với ID đã cho.");
            // Ghi log thông báo lỗi
            System.out.println("Error: Không tìm thấy sách nào với ID đã cho.");
            return "redirect:/appointments"; // Redirect back to the appointments page
        }

        try {
            Appointment appointment = appointmentService.createAppointment(user, books, borrowDate, returnDate, notes);
            redirectAttributes.addFlashAttribute("message", "Đặt lịch hẹn thành công!");
            // Ghi log thông báo thành công
            System.out.println("Message: Đặt lịch hẹn thành công!");
            return "redirect:/index"; // Redirect to the home page
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Ghi log thông báo lỗi từ exception
            System.out.println("Error: " + e.getMessage());
            return "redirect:/appointments"; // Redirect back to the appointments page
        }
    }
}

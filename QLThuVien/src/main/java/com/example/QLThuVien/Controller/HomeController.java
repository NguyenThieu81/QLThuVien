package com.example.QLThuVien.Controller;

import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.*;
import com.example.QLThuVien.services.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor // Sử dụng RequiredArgsConstructor để tự động tiêm các phụ thuộc
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final AppointmentService appointmentService;

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }
    @GetMapping("/search")
    public String searchBooks(@RequestParam String query,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Page<Book> bookPage = bookService.searchBooks(query, PageRequest.of(page, 16));
        return setupModelForBooks(model, bookPage, page);
    }
    @GetMapping("/index")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        // Gọi phương thức để lấy thông báo cho người dùng hiện tại
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
// Đếm số lượng thông báo chưa đọc cho người dùng hiện tại
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();
        // Thêm thông báo vào model
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount); // Đếm số thông báo
        return setupModelForBooks(model, bookService.getAllBooks(PageRequest.of(page, 16)), page);
    }

    @GetMapping("/books/author/{authorId}")
    public String getBooksByAuthor(@PathVariable Long authorId, @RequestParam(defaultValue = "0") int page, Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);
        return setupModelForBooks(model, bookService.getBooksByAuthor(authorId, PageRequest.of(page, 16)), page);
    }

    @GetMapping("/books/category/{categoryId}")
    public String getBooksByCategory(@PathVariable Long categoryId, @RequestParam(defaultValue = "0") int page, Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);
        return setupModelForBooks(model, bookService.getBooksByCategory(categoryId, PageRequest.of(page, 16)), page);
    }

    private String setupModelForBooks(Model model, Page<Book> bookPage, int page) {
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalBooks", bookPage.getTotalElements());
        return "home/index";
    }
    // Thêm phương thức để hiển thị chi tiết sách
    @GetMapping("/book/{id}")
    public String getBookDetail(@PathVariable Long id, Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);
        Book book = bookService.getById(id);
        if (book == null) {
            return "redirect:/index"; // Hoặc bạn có thể hiển thị một trang lỗi
        }
        model.addAttribute("book", book);
        return "home/detail"; // Chuyển đến template chi tiết sách
    }

    @GetMapping("/notification/{id}")
    public String getNotificationDetail(@PathVariable Long id, Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);
        Notification notification = notificationService.getNotificationById(id);
        notificationService.markNotificationAsRead(id);

        if (notification == null) {
            return "redirect:/index"; // Hoặc bạn có thể hiển thị một trang lỗi
        }

        // Lấy thông tin từ Appointment
        Appointment appointment = notification.getAppointment();

        // Kiểm tra trạng thái của lịch hẹn
        if (appointment != null &&
                (appointment.getStatus() == AppointmentStatus.COMPLETED ||
                        appointment.getStatus() == AppointmentStatus.CONFIRMED)) {

            // Nếu trạng thái thỏa mãn, thêm thông tin cuộc hẹn vào model
            model.addAttribute("appointment", appointment); // Thêm appointment vào model

            // Lấy danh sách sách từ lịch hẹn
            List<AppointmentBook> appointmentBooks = new ArrayList<>(appointment.getAppointmentBooks());
            model.addAttribute("appointmentBooks", appointmentBooks); // Thêm danh sách appointmentBooks vào model

            // Tính tổng tiền
            long totalCost = appointment.getTotalCost(); // Giả sử bạn đã lưu tổng tiền trong Appointment
            model.addAttribute("totalCost", totalCost); // Thêm tổng tiền vào model
        } else {
            // Nếu trạng thái không thỏa mãn, không thêm thông tin cuộc hẹn vào model
            model.addAttribute("appointment", null); // Hoặc không cần thêm gì cả
        }

        // Xử lý thông báo xác nhận mượn sách
        if ("BOOK_BORROW".equals(notification.getType())) {
            // Chuyển hướng đến trang bookshelf
            return "redirect:/bookshelf"; // Chuyển hướng đến trang bookshelf
        }
        if ("BOOK_RETURN".equals(notification.getType())) {
            // Chuyển hướng đến trang bookshelf
            return "redirect:/history"; // Chuyển hướng đến trang bookshelf
        }

        // Thêm thông báo vào model
        model.addAttribute("notification", notification); // Thêm thông báo vào model
        return "home/notification_detail"; // Chuyển đến template chi tiết thông báo
    }

    @PostMapping("/notifications/mark-all-read")
    public String markAllNotificationsAsRead(HttpServletRequest request) {
        notificationService.markAllNotificationsAsReadForCurrentUser ();

        // Chuyển hướng về trang index
        return "redirect:/index";
    }


    @GetMapping("/bookshelf")
    public String getBookshelf(Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);

        // Lấy thông tin người dùng hiện tại
        User currentUser  = userService.getCurrentUser (); // Giả sử bạn có phương thức này để lấy người dùng hiện tại

        // Lấy danh sách các lịch hẹn của người dùng
        Set<Appointment> appointments = appointmentService.getAppointmentsByUser (currentUser );

        // Tạo danh sách để lưu các AppointmentBook và tổng tiền
        List<AppointmentBook> appointmentBooks = new ArrayList<>();
        long totalCost = 0;

        // Duyệt qua các lịch hẹn để lấy AppointmentBook chỉ với trạng thái COMPLETED
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                appointmentBooks.addAll(appointment.getAppointmentBooks());
                totalCost += appointment.getTotalCost(); // Cộng dồn tổng tiền
            }
        }

        // Thêm dữ liệu vào mô hình
        model.addAttribute("appointmentBooks", appointmentBooks);
        model.addAttribute("totalCost", totalCost);

        return "home/bookshelf"; // Chuyển đến template bookshelf
    }
    @GetMapping("/history")
    public String getReturnHistory(Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);

        // Lấy thông tin người dùng hiện tại
        User currentUser  = userService.getCurrentUser ();

        // Lấy danh sách tất cả các lịch hẹn đã trả
        List<Appointment> allReturnedAppointments = appointmentService.getAllReturnedAppointments();

        // Lọc danh sách để chỉ lấy các lịch hẹn của người dùng hiện tại
        List<Appointment> returnedAppointments = allReturnedAppointments.stream()
                .filter(appointment -> appointment.getUser ().equals(currentUser ))
                .collect(Collectors.toList());

        // Thêm dữ liệu vào mô hình
        model.addAttribute("returnedAppointments", returnedAppointments); // Thay đổi tên biến để rõ ràng hơn

        return "home/history"; // Chuyển đến template history
    }
    @GetMapping("/about")
    public String about(Model model) {
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser ();
        long unreadNotificationCount = notificationService.countUnreadNotificationsForCurrentUser ();

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", unreadNotificationCount);

        return "home/about"; // Chuyển đến template giới thiệu
    }

}
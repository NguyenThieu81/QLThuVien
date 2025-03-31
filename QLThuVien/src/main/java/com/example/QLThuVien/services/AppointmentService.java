package com.example.QLThuVien.services;

import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.*;
import com.example.QLThuVien.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;


    @Autowired
    private BookService bookService;

    @Autowired
    private NotificationService notificationService;
    @Autowired private AdminNotificationService adminNotificationService;
    public Page<Appointment> findAppointmentsByUsername(String username, Pageable pageable) {
        return appointmentRepository.findByUsername(username, pageable);
    }

    public List<Object[]> getMostBorrowedBooks() {
        return appointmentRepository.findMostBorrowedBooks();
    }

    public List<Appointment> getBorrowedAppointmentsByUsername(String username) {
        return appointmentRepository.findBorrowedAppointmentsByUsername(username);
    }

    public List<Appointment> getReturnedAppointmentsByUsername(String username) {
        return appointmentRepository.findReturnedAppointmentsByUsername(username);
    }

    @Transactional
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    @Transactional
    public Appointment createAppointment(User user, Set<Book> books, LocalDateTime borrowDate,
                                         LocalDateTime returnDate, String notes) {
        if (borrowDate == null || returnDate == null) {
            throw new IllegalArgumentException("Ngày mượn và ngày trả không được để trống.");
        }
        if (borrowDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày mượn không thể trước hiện tại.");
        }
        if (returnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Ngày trả phải sau ngày mượn.");
        }

        Appointment appointment = new Appointment();
        appointment.setUser (user);
        appointment.setBorrowDate(borrowDate);
        appointment.setReturnDate(returnDate);
        appointment.setNote(notes);

        long totalCost = 0; // Biến để lưu tổng tiền cho tất cả các cuốn sách

        for (Book book : books) {
            AppointmentBook appointmentBook = new AppointmentBook();
            long cost = calculateCost(borrowDate, returnDate, book); // Tính toán tổng tiền cho cuốn sách
            appointmentBook.setBook(book);
            appointmentBook.setCost(cost);
            appointment.addAppointmentBook(appointmentBook); // Thêm vào danh sách sách trong lịch hẹn
            totalCost += cost; // Cộng dồn tổng tiền
        }
        // Lưu appointment vào cơ sở dữ liệu
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Gửi thông báo cho admin
        String adminMessage = "Người dùng " + user.getUsername() + " đã đặt lịch hẹn với ngày hẹn :"
                + appointment.getFormattedBorrowDate()+" và ngày trả :"+appointment.getFormattedReturnDate() ;
        adminNotificationService.sendAdminNotification(savedAppointment, adminMessage); // Gửi thông báo cho admin với thông tin cuộc hẹn
        appointment.setTotalCost(totalCost); // Lưu tổng tiền vào appointment
        return savedAppointment; // Trả về appointment đã lưu

    }
    // Phương thức tính toán tổng tiền cho cuốn sách
    private long calculateCost(LocalDateTime borrowDate, LocalDateTime returnDate, Book book) {
        long days = ChronoUnit.DAYS.between(borrowDate, returnDate);
        long cost;

        if (days <= 7) {
            cost = days * 5000; // 5000 VNĐ per day for the first 7 days
        } else {
            cost = (7 * 5000) + ((days - 7) * 8000); // 8000 VNĐ per day after 7 days
        }

        return cost;
    }
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
    public List<Appointment> getAllBorrowedAppointments() {
        return appointmentRepository.findAllWithBorrowedAppointments(); // Gọi phương thức mới
    }

    public List<Appointment> getAllReturnedAppointments() {
        return appointmentRepository.findAllWithReturnedAppointments(); // Gọi phương thức mới
    }

    public Set<Appointment> getAppointmentsByUser (User user) {
        return new HashSet<>(appointmentRepository.findAllByUser (user)); // Giả sử bạn đã tạo phương thức này trong repository
    }
    // Lấy tất cả lịch hẹn (phân trang)
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAllWithBooks(pageable);
    }


    // Lấy chi tiết lịch hẹn theo ID
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch hẹn"));
    }

    // Xác nhận lịch hẹn
    @Transactional
    public Appointment confirmAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Kiểm tra trạng thái hiện tại của lịch hẹn
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Chỉ được xác nhận lịch hẹn ở trạng thái chờ");
        }

        // Cập nhật trạng thái
        appointment.setStatus(AppointmentStatus.CONFIRMED);


        // Tạo thông báo cho người dùng
        Notification notification = new Notification();
        notification.setUser (appointment.getUser ());
        notification.setAppointment(appointment);
        notification.setMessage("Lịch hẹn của bạn đã được xác nhận!");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        // Trong phương thức confirmAppointment
        notification.setType("APPOINTMENT_CONFIRMATION");

        // Gửi thông báo
        notificationService.sendNotification(notification);

        return appointmentRepository.save(appointment);
    }

    // Hủy lịch hẹn
    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Kiểm tra trạng thái hiện tại của lịch hẹn
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy lịch hẹn đã hoàn thành");
        }

        // Cập nhật trạng thái
        appointment.setStatus(AppointmentStatus.CANCELLED);


        return appointmentRepository.save(appointment);
    }
    @Transactional
    public Appointment confirmBorrow(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Kiểm tra trạng thái hiện tại của lịch hẹn
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Chỉ có thể xác nhận mượn sách khi lịch hẹn đã được xác nhận");
        }

        // Đánh dấu trạng thái của cuộc hẹn là chưa trả
        appointment.setReturned(false); // Đánh dấu là chưa trả

        // Duyệt qua từng cuốn sách trong lịch hẹn và gọi phương thức borrowBook
        for (AppointmentBook appointmentBook : appointment.getAppointmentBooks()) {
            Book book = appointmentBook.getBook();
            bookService.borrowBook(book); // Trừ số lượng sách
        }

        // Cập nhật trạng thái của lịch hẹn thành "hoàn thành"
        appointment.setStatus(AppointmentStatus.COMPLETED);

        // Tạo thông báo cho người dùng
        Notification notification = new Notification();
        notification.setUser (appointment.getUser ());
        notification.setAppointment(appointment);

        // Tạo thông điệp với tên sách
        StringBuilder bookTitles = new StringBuilder("Mượn sách thành công! Sách: ");
        for (AppointmentBook appointmentBook : appointment.getAppointmentBooks()) {
            bookTitles.append(appointmentBook.getBook().getTitle()).append(", ");
        }
        // Xóa dấu phẩy và khoảng trắng cuối cùng
        if (bookTitles.length() > 0) {
            bookTitles.setLength(bookTitles.length() - 2);
        }

        notification.setMessage(bookTitles.toString());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setType("BOOK_BORROW");

        // Gửi thông báo
        notificationService.sendNotification(notification);

        // Lưu lại thay đổi trạng thái của lịch hẹn
        return appointmentRepository.save(appointment);
    }
    @Transactional
    public Appointment confirmReturn(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Kiểm tra xem có bất kỳ cuốn sách nào chưa được trả
        if (appointment.isReturned()) {
            throw new IllegalStateException("Tất cả sách đã được trả.");
        }

        // Cập nhật trạng thái của từng sách trong cuộc hẹn
        for (AppointmentBook appointmentBook : appointment.getAppointmentBooks()) {
            // Đánh dấu là đã trả
            Book book = appointmentBook.getBook();
            bookService.returnBook(book); // Cập nhật số lượng sách trong kho
        }

        // Đánh dấu trạng thái của cuộc hẹn là đã trả
        appointment.setReturned(true); // Đánh dấu là đã trả

        // Cập nhật trạng thái của lịch hẹn thành "Đã trả"
        appointment.setStatus(AppointmentStatus.RETURNED);

        // Tạo thông báo cho người dùng
        Notification notification = new Notification();
        notification.setUser (appointment.getUser ());
        notification.setAppointment(appointment);
        notification.setMessage("Bạn đã trả sách thành công!");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setType("BOOK_RETURN");

        // Gửi thông báo
        notificationService.sendNotification(notification);

        // Lưu lại thay đổi trạng thái của lịch hẹn
        return appointmentRepository.save(appointment);
    }

}
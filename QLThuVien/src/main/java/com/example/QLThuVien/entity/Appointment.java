package com.example.QLThuVien.entity;

import com.example.QLThuVien.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "appointment")
@EqualsAndHashCode(exclude = "appointmentBooks")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppointmentBook> appointmentBooks = new HashSet<>();


    private LocalDateTime borrowDate; // Date when the books are borrowed
    private LocalDateTime returnDate; // Date when the books are expected to be returned

    @Size(max = 5000, message = "Ghi chú phải ít hơn 5000 ký tự")
    @Column(name = "note")
    private String note;
    private boolean isReturned; // Trạng thái đã trả hay chưa

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private long totalCost;
    public String getFormattedBorrowDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return borrowDate != null ? borrowDate.format(formatter) : null;
    }
    public void addAppointmentBook(AppointmentBook appointmentBook) {
        appointmentBooks.add(appointmentBook);
        appointmentBook.setAppointment(this); // Thiết lập mối quan hệ hai chiều
        totalCost += appointmentBook.getCost(); // Cập nhật tổng chi phí
    }
    public String getFormattedReturnDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return returnDate != null ? returnDate.format(formatter) : null;
    }

    // Phương thức để kiểm tra trạng thái của cuộc hẹn
    public String getStatus1() {
        // Nếu tất cả các cuốn sách chưa được trả, trả về "Đang mượn"
        if (!isReturned()) {
            return "Đang mượn";
        }
        // Nếu tất cả các cuốn sách đã được trả, trả về "Đã trả"
        return "Đã trả";
    }
    public String getStatusInVietnamese() {
        switch (status) {
            case PENDING:
                return "Chờ xử lý";
            case CONFIRMED:
                return "Đã xác nhận";
            case COMPLETED:
                return "Đã hoàn thành";
            case CANCELLED:
                return "Đã hủy";
            case RETURNED:
                return "Đã Trả";
            default:
                return "Không xác định";
        }
    }
    public long getTotalRevenueIfStatusIsCompleteOrReturned() {
        if (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.RETURNED) {
            return totalCost;
        }
        return 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, user, borrowDate, returnDate); // Chỉ sử dụng các thuộc tính không tham chiếu đến AppointmentBook
    }

}
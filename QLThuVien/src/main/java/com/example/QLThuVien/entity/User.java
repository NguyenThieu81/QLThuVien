package com.example.QLThuVien.entity;

import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.BorrowRecordService;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, unique = true)
    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    private String username;

    @Column(name = "password", length = 250)
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "email", length = 50, unique = true)
    @NotBlank(message = "Email is required")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowRecord> borrowRecords = new HashSet<>(); // Các bản ghi mượn

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReturnRecord> returnRecords = new HashSet<>(); // Các bản ghi trả sách

    public int getBorrowedBookCount() {
        return borrowRecords.size();
    }

    public void borrowBook(Book book, BorrowRecordService borrowRecordService) {
        // Kiểm tra xem sách đã được mượn hay chưa
        if (borrowRecords.stream().anyMatch(borrowRecord -> borrowRecord.getBook().equals(book))) {
            throw new IllegalStateException("Sách đã được mượn.");
        }

        // Kiểm tra số lượng sách đã mượn
        if (borrowRecords.size() >= 3) {
            throw new IllegalStateException("Bạn đã mượn tối đa 3 quyển sách.");
        }

        // Tạo bản ghi mượn mới
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser (this); // Gán người dùng
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setIsReturned(false); // Thiết lập trạng thái là chưa trả
        borrowRecord.setTransactionDate(LocalDateTime.now()); // Thiết lập thời gian giao dịch


        // Lưu bản ghi mượn vào cơ sở dữ liệu
        borrowRecordService.save(borrowRecord);

        // Thêm bản ghi vào tập hợp của người dùng
        borrowRecords.add(borrowRecord);

    }

    public void returnBook(Book book, BookService bookService,BorrowRecordService borrowRecordService) {
        // Tìm bản ghi mượn tương ứng
        BorrowRecord recordToReturn = borrowRecords.stream()
                .filter(record -> record.getBook().equals(book))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No borrow record found for this book"));

        // Thêm bản ghi trả sách
        ReturnRecord returnRecord = new ReturnRecord();
        returnRecord.setUser (this);
        returnRecord.setBook(book);
        returnRecord.setReturnDate(LocalDateTime.now());
        returnRecord.setTransactionDate(LocalDateTime.now()); // Thiết lập thời gian giao dịch
// Đánh dấu bản ghi mượn là đã trả
        recordToReturn.setIsReturned(true); // Cập nhật trạng thái là đã trả
// Lưu bản ghi trả sách vào cơ sở dữ liệu
        borrowRecordService.save(returnRecord); // Đảm bảo rằng phương thức này lưu bản ghi trả sách
        // Cập nhật số lượng sách
        bookService.returnBook(book);
    }

    public Set<ReturnRecord> getReturnedRecords() {
        return returnRecords; // Trả về tất cả các bản ghi trả sách
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.ReturnRecord;
import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.entity.BorrowRecord; // Import BorrowRecord nếu cần
import com.example.QLThuVien.services.AuthorService;
import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.CategoryService;
import com.example.QLThuVien.services.UserService;
import com.example.QLThuVien.services.BorrowRecordService; // Import BorrowRecordService
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor // Sử dụng RequiredArgsConstructor để tự động tiêm các phụ thuộc
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final UserService userService;
    private final BorrowRecordService borrowRecordService; // Thêm BorrowRecordService

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String home(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "home/index";
    }

    // Thêm phương thức để hiển thị chi tiết sách
    @GetMapping("/book/{id}")
    public String getBookDetail(@PathVariable Long id, Model model) {
        Book book = bookService.getById(id);
        if (book == null) {
            return "redirect:/index"; // Hoặc bạn có thể hiển thị một trang lỗi
        }
        model.addAttribute("book", book);
        return "home/detail"; // Chuyển đến template chi tiết sách
    }

    @PostMapping("/book/{id}/borrow")
    public String borrowBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser  = userService.getCurrentUser ();
        Book book = bookService.getById(id);

        try {
            // Gọi phương thức mượn sách từ lớp User
            currentUser .borrowBook(book, borrowRecordService);
// Trừ số lượng sách
            bookService.borrowBook(book);
            redirectAttributes.addFlashAttribute("message", "Mượn sách thành công: " + book.getTitle());
            return "redirect:/borrow"; // Chuyển hướng đến trang borrow
        } catch (IllegalStateException e) {
            // Xử lý thông báo lỗi
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/borrow"; // Chuyển hướng đến trang borrow với thông báo lỗi
        }
    }
    @PostMapping("/book/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser  = userService.getCurrentUser ();
        Book book = bookService.getById(id);

        try {
            // Gọi phương thức trả sách từ User
            currentUser .returnBook(book, bookService, borrowRecordService); // Truyền borrowRecordService vào

            redirectAttributes.addFlashAttribute("message", "Trả sách thành công: " + book.getTitle());
            return "redirect:/borrow"; // Chuyển hướng đến trang borrow
        } catch (IllegalStateException e) {
            // Xử lý thông báo lỗi
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/borrow"; // Chuyển hướng đến trang borrow với thông báo lỗi
        }
    }
    // Thêm phương thức để hiển thị các sách đang mượn
    @GetMapping("/borrow")
    public String viewBorrowedBooks(Model model) {
        User currentUser  = userService.getCurrentUser ();
        List<BorrowRecord> borrowedBooks = borrowRecordService.findByUser (currentUser );
        List<ReturnRecord> returnedBooks = borrowRecordService.findReturnedRecordsByUser (currentUser ); // Lấy danh sách bản ghi trả sách

        model.addAttribute("borrowedBooks", borrowedBooks);
        model.addAttribute("returnedBooks", returnedBooks); // Thêm danh sách bản ghi trả sách vào model
        return "home/borrow"; // Chuyển đến template hiển thị sách đang mượn
    }

}
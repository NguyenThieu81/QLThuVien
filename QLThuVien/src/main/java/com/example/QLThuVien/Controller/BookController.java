package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.CategoryService;
import com.example.QLThuVien.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/book")
public class BookController {
    private static final String UPLOADED_DIR = "static/images/";

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AuthorService authorService;

    @GetMapping
    public String showAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "admin/books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/book-add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute Book book,
                          @RequestParam(value = "image", required = false) MultipartFile file,
                          RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOADED_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                book.setImagePath("/images/" + file.getOriginalFilename());
            }

            bookService.save(book);
            redirectAttributes.addFlashAttribute("message", "Sách đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/book"; // Chuyển hướng về trang sách
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getById(id);
        if (book == null) {
            // Xử lý nếu không tìm thấy sách
            return "redirect:/admin/book"; // Hoặc hiển thị thông báo lỗi
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/book-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute Book book,
                             @RequestParam(value = "image", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOADED_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                book.setImagePath("/images/" + file.getOriginalFilename());
            }

            bookService.update(book);
            redirectAttributes.addFlashAttribute("message", "Sách đã được cập nhật thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/book"; // Chuyển hướng về trang sách
    }


    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Sách đã được xóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/book"; // Redirect back to the book list
    }

}
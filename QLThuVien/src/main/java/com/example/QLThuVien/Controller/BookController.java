package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/book")
public class BookController {
    private static final String UPLOADED_DIR = "static/images/";


    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách sách
    @GetMapping
    public String showAllBooks(Model model) {
        //List<Book> books = bookService.getALlBooks();
       // model.addAttribute("books", books);
        return "book/list";
    }

    // Hiển thị form thêm sách
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/add";
    }

    // Xử lý thêm sách
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                          @RequestParam("image") MultipartFile file,
                          BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/add";
        }

        try {
            // Lưu ảnh sách
            if (!file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOADED_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                book.setImagePath("/images/" + file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bookService.addBook(book);
        return "redirect:/admin/book";
    }

    // Hiển thị form chỉnh sửa sách
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }
        return "redirect:/admin/book";
    }

    // Xử lý cập nhật sách
    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable("id") Long id, @RequestParam("image") MultipartFile file,
                             @Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }

        try {
            // Lưu ảnh sách
            if (!file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOADED_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                book.setImagePath("/images/" + file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bookService.updateBook(book);
        return "redirect:/admin/book";
    }
    @GetMapping("/book/detail/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String bookDetail(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            model.addAttribute("book", book);
            return "book/detail";
        }
        return "redirect:/";  // Quay lại trang chủ nếu không tìm thấy sách
    }

    // Xử lý xóa sách
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteBook(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sách đã được xóa thành công!");
        return "redirect:/admin/book";
    }
}

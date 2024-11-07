package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Author;
import com.example.QLThuVien.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public String showAllAuthors(Model model) {
        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "admin/authors";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/author-add";
    }

    @PostMapping("/add")
    public String addAuthor(@Valid @ModelAttribute Author author, RedirectAttributes redirectAttributes) {
        try {
            authorService.save(author);
            redirectAttributes.addFlashAttribute("message", "Tác giả đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/author"; // Chuyển hướng về trang sách
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Author author = authorService.getById(id);
        model.addAttribute("author", author);
        return "admin/author-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAuthor(@PathVariable Long id, @Valid @ModelAttribute Author author,RedirectAttributes redirectAttributes) {

        try {
            authorService.update(author);
            redirectAttributes.addFlashAttribute("message", "Tác giả đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/author"; // Chuyển hướng về trang sách
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (authorService.hasBooks(id)) {
                redirectAttributes.addFlashAttribute("deleteError", "Không thể xóa tác giả này vì nó đang có sách.");
            } else {
                authorService.delete(id);
                redirectAttributes.addFlashAttribute("message", "Tác giả đã được xóa thành công");
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("deleteError", "Không thể xóa tác giả này vì nó đang có sách.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/author"; // Redirect back to the book list
    }
}
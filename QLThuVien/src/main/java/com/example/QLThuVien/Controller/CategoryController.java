package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.services.CategoryService;
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
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String showAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute Category category, RedirectAttributes redirectAttributes) {

        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("message", "Thể loại đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/category"; // Chuyển hướng về trang sách
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id);
        model.addAttribute("category", category);
        return "admin/category-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                              @Valid @ModelAttribute Category category,RedirectAttributes redirectAttributes) {

        try {
            categoryService.update(category);
            redirectAttributes.addFlashAttribute("message", "Thể loại đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/category"; // Chuyển hướng về trang sách
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (categoryService.hasBooks(id)) {
                redirectAttributes.addFlashAttribute("deleteError", "Không thể xóa thể loại này vì nó đang có sách.");
            } else {
                categoryService.delete(id);
                redirectAttributes.addFlashAttribute("message", "Thể loại đã được xóa thành công");
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("deleteError", "Không thể xóa thể loại này vì nó đang có sách.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/category"; // Redirect back to the category list
    }
}
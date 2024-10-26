package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.services.BookService;
import com.example.QLThuVien.services.CategoryService;
import com.example.QLThuVien.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    // Trang dashboard admin
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("message", "Chào mừng đến với trang quản lý admin!");
        return "admin/dashboard2";
    }



}

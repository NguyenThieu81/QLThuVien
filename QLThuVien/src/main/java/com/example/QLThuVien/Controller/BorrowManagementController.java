package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.services.TransactionService;
import com.example.QLThuVien.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/borrow-management")
public class BorrowManagementController {

    private final TransactionService transactionService;

    public BorrowManagementController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String viewTransactionSummary(Model model) {
        List<Object[]> transactionSummary = transactionService.getTransactionSummary(); // Fetch transaction summary
        model.addAttribute("transactionSummary", transactionSummary); // Add to model
        return "admin/borrow_management"; // Return to the borrow management template
    }
}
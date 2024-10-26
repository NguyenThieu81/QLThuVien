package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.services.BorrowRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/borrowRecords")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping
    public String showAllBorrowRecords(Model model) {
        List<BorrowRecord> borrowRecords = borrowRecordService.getAllBorrowRecords();
        model.addAttribute("borrowRecords", borrowRecords);
        return "borrowRecord/list";
    }

    @GetMapping("/add")
    public String addBorrowRecordForm(Model model) {
        model.addAttribute("borrowRecord", new BorrowRecord());
        return "borrowRecord/add";
    }

    @PostMapping("/add")
    public String addBorrowRecord(@Valid @ModelAttribute("borrowRecord") BorrowRecord borrowRecord, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "borrowRecord/add";
        }
        borrowRecordService.addBorrowRecord(borrowRecord);
        return "redirect:/borrowRecords";
    }

    @GetMapping("/edit/{id}")
    public String editBorrowRecordForm(@PathVariable("id") Long id, Model model) {
        BorrowRecord borrowRecord = borrowRecordService.getBorrowRecordById(id);
        if (borrowRecord != null) {
            model.addAttribute("borrowRecord", borrowRecord);
            return "borrowRecord/edit";
        }
        return "redirect:/borrowRecords";
    }

    @PostMapping("/edit/{id}")
    public String updateBorrowRecord(@PathVariable("id") Long id, @Valid @ModelAttribute("borrowRecord") BorrowRecord borrowRecord, BindingResult result) {
        if (result.hasErrors()) {
            return "borrowRecord/edit";
        }
        borrowRecordService.updateBorrowRecord(borrowRecord);
        return "redirect:/borrowRecords";
    }

    @GetMapping("/delete/{id}")
    public String deleteBorrowRecord(@PathVariable("id") Long id) {
        borrowRecordService.deleteBorrowRecord(id);
        return "redirect:/borrowRecords";
    }
}

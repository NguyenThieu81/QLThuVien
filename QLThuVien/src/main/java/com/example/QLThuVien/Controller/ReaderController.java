package com.example.QLThuVien.Controller;

import com.example.QLThuVien.entity.Reader;
import com.example.QLThuVien.services.ReaderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @GetMapping
    public String showAllReaders(Model model) {
        List<Reader> readers = readerService.getAllReaders();
        model.addAttribute("readers", readers);
        return "reader/list";
    }

    @GetMapping("/add")
    public String addReaderForm(Model model) {
        model.addAttribute("reader", new Reader());
        return "reader/add";
    }

    @PostMapping("/add")
    public String addReader(@Valid @ModelAttribute("reader") Reader reader, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "reader/add";
        }
        readerService.addReader(reader);
        return "redirect:/readers";
    }

    @GetMapping("/edit/{id}")
    public String editReaderForm(@PathVariable("id") Long id, Model model) {
        Reader reader = readerService.getReaderById(id);
        if (reader != null) {
            model.addAttribute("reader", reader);
            return "reader/edit";
        }
        return "redirect:/readers";
    }

    @PostMapping("/edit/{id}")
    public String updateReader(@PathVariable("id") Long id, @Valid @ModelAttribute("reader") Reader reader, BindingResult result) {
        if (result.hasErrors()) {
            return "reader/edit";
        }
        readerService.updateReader(reader);
        return "redirect:/readers";
    }

    @GetMapping("/delete/{id}")
    public String deleteReader(@PathVariable("id") Long id) {
        readerService.deleteReader(id);
        return "redirect:/readers";
    }
}

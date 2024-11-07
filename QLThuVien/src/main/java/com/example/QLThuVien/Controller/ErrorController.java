package com.example.QLThuVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";  // This should be the name of your 403 error page
    }
}
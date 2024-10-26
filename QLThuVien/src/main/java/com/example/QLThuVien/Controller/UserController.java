package com.example.QLThuVien.Controller;


import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "users/signup";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @RequestParam("confirmPassword") String confirmPassword,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        // Kiểm tra mật khẩu và mật khẩu xác nhận
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Mật khẩu không khớp.");
        }
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/signup";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());

        model.addAttribute("successMessage", "Đăng kí tài khoản thành công!.");
        return "redirect:/login";
    }
    @GetMapping("/logout")
    public String logoutPage(Model model) {
        model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        return "redirect:/";  // Chuyển hướng về trang chủ.
    }

    @PostMapping("/delete-account")
    public String deleteUserAccount(@AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin người dùng hiện tại từ đối tượng UserDetails
        String username = userDetails.getUsername();

        // Xóa dữ liệu của người dùng từ cơ sở dữ liệu
        userService.deleteUserByUsername(username);

        // Đăng xuất người dùng sau khi xóa tài khoản
        return "redirect:/logout";
    }



}

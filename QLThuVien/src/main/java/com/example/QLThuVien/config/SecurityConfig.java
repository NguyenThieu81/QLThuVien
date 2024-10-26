package com.example.QLThuVien.config;
import com.example.QLThuVien.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Trả về dịch vụ UserService trực tiếp.
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sử dụng BCrypt để mã hóa mật khẩu.
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                response.sendRedirect(request.getContextPath() + "/error/403"); // Xử lý quyền truy cập bị từ chối.
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService()); // Thiết lập UserDetailsService.
        auth.setPasswordEncoder(passwordEncoder()); // Thiết lập PasswordEncoder.
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/register", "/error").permitAll() // Không yêu cầu xác thực.
                        .requestMatchers("/admin/**","/admin/book/**","/admin/category/**").hasRole("ADMIN") // Chỉ ADMIN có quyền.
                        .requestMatchers("/api/**").permitAll() // API mở cho mọi người.
                        .anyRequest().authenticated() // Yêu cầu xác thực cho các yêu cầu khác.
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Chỉ định trang đăng nhập.
                        .loginProcessingUrl("/login") // URL xử lý đăng nhập.
                        .successHandler(customLoginSuccessHandler) // Trang sau đăng nhập thành công.
                        .failureUrl("/login?error") // Trang khi đăng nhập thất bại.
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Chuyển hướng sau khi đăng xuất.
                        .deleteCookies("JSESSIONID") // Xóa cookie JSESSIONID.
                        .invalidateHttpSession(true) // Hủy phiên đăng nhập.
                        .clearAuthentication(true) // Xóa xác thực.
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("hutech")
                        .rememberMeCookieName("hutech")
                        .tokenValiditySeconds(24 * 60 * 60) // Thời gian nhớ đăng nhập là 24 giờ.
                        .userDetailsService(userDetailsService())
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1) // Chỉ cho phép 1 phiên đăng nhập.
                        .expiredUrl("/login") // Chuyển hướng khi phiên hết hạn.
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403") // Trang xử lý truy cập bị từ chối.
                        .accessDeniedHandler(customAccessDeniedHandler()) // Xử lý ngoại lệ truy cập bị từ chối.
                );

        return http.build(); // Trả về cấu hình chuỗi bảo mật.
    }
}


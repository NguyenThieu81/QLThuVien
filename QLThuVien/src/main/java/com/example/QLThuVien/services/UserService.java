package com.example.QLThuVien.services;

import jakarta.validation.Validator;
import com.example.QLThuVien.UserRole;
import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.entity.Role;
import com.example.QLThuVien.repository.IRoleRepository;
import com.example.QLThuVien.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private Validator validator;
    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        log.info("Saving new user: {}", user.getUsername());
    }
    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    // Tìm vai trò USER trong cơ sở dữ liệu bằng tên
                    Role role = roleRepository.findByName(UserRole.USER.getName()); // Change here
                    if (role != null) {
                        user.getRoles().add(role);
                        userRepository.save(user);
                        log.info("Gán vai trò USER cho người dùng: {}", user.getUsername());
                    } else {
                        log.error("Không thể gán vai trò USER vì nó không tồn tại.");
                    }
                },
                () -> { throw new UsernameNotFoundException("User  not found"); }
        );
    }
    // Tải thông tin chi tiết người dùng để xác thực.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // Đảm bảo rằng thuộc tính "username" chứa tên người dùng
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) throws
            UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id); // Giả sử bạn đã định nghĩa phương thức này trong IUserRepository
    }
    // Xóa người dùng dựa trên username
    public void deleteUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    // Phương thức để lấy người dùng hiện tại
    public User getCurrentUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }


    public User saveOrUpdateUserFromGoogle(String username, String email) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(email);
            return userRepository.save(user);
        } else {
            User newUser = new User();
            newUser.setUsername(username); // Sử dụng thuộc tính "name" cho username
            newUser.setEmail(email);
            newUser.setFromGoogle(true); // Đặt cờ từ Google
            newUser.setRoles(Set.of(roleRepository.findByName(UserRole.USER.getName())));
            return userRepository.save(newUser);
        }
    }



}

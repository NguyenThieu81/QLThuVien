package com.example.QLThuVien.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên phải ít hơn 100 ký tự")
    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại không hợp lệ")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Email(message = "Email không hợp lệ")
    @Column(name = "email")
    private String email;


}


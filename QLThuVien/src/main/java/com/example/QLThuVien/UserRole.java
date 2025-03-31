package com.example.QLThuVien;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    ADMIN(1, "ROLE_ADMIN"), // Vai trò quản trị viên
    USER(2, "ROLE_USER"); // Vai trò người dùng

    public final long value;
    public final String name;

    public long getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
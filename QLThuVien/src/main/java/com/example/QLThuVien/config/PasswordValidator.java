package com.example.QLThuVien.config;

import com.example.QLThuVien.ValidPassword;
import com.example.QLThuVien.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, User> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user.isFromGoogle()) {
            return true;
        } else {
            return user.getPassword() != null && !user.getPassword().isEmpty();
        }
    }
}

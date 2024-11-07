package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }
    public boolean hasBooks(Long categoryId) {
        Category category = getById(categoryId);
        return category != null && category.getBooks() != null && !category.getBooks().isEmpty();
    }
    public void update(Category category) {
        categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
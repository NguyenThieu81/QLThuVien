package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.Author;
import com.example.QLThuVien.entity.Category;
import com.example.QLThuVien.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }
    public boolean hasBooks(Long authorId) {
        Author author = getById(authorId);
        return author != null && author.getBooks() != null && !author.getBooks().isEmpty();
    }
    public void save(Author author) {
        authorRepository.save(author);
    }

    public void update(Author author) {
        authorRepository.save(author);
    }

    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}
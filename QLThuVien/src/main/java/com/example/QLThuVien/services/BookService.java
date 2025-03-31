package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public Set<Book> findAllByIds(Set<Long> ids) {
        Set<Book> books = new HashSet<>(bookRepository.findAllById(ids));
        if (books.size() != ids.size()) {
            System.out.println("Warning: Not all book IDs were found in the database.");
        }
        return books;
    }

    public Book getById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public void update(Book book) {
        bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
    public long countTotalBooks() {
        return bookRepository.count();
    }
    public Page<Book> getBooksByAuthor(Long authorId, Pageable pageable) {
        return bookRepository.findByAuthorId(authorId, pageable);
    }

    public Page<Book> getBooksByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchBooks(query, pageable);
    }
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    // Phương thức để trừ số lượng sách khi mượn
    public void borrowBook(Book book) {
        if (book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1); // Giảm số lượng sách đi 1
            bookRepository.save(book);
        } else {
            throw new IllegalStateException("Không còn sách để mượn.");
        }
    }

    public void returnBook(Book book) {
        book.setQuantity(book.getQuantity() + 1); // Tăng số lượng sách lên 1
        bookRepository.save(book); // Lưu thay đổi vào cơ sở dữ liệu
    }



}
package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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
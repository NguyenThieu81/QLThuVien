package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.Book;
import com.example.QLThuVien.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Lấy tất cả các sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lấy sách theo ID, trả về ngoại lệ nếu không tìm thấy
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    // Thêm sách mới
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    // Xóa sách theo ID
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id " + id);
        }
        bookRepository.deleteById(id);
    }

    // Cập nhật sách
    public Book updateBook(Book updatedBook) {
        Optional<Book> existingBookOpt = bookRepository.findById(updatedBook.getId());
        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setCategory(updatedBook.getCategory());
            existingBook.setDescription(updatedBook.getDescription());

            // Thêm các thuộc tính khác nếu cần
            return bookRepository.save(existingBook);
        } else {
            throw new RuntimeException("Book not found with id " + updatedBook.getId());
        }
    }
}

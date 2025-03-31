package com.example.QLThuVien.repository;
import com.example.QLThuVien.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Phương thức tìm sách theo tác giả với phân trang
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);

    // Phương thức tìm sách theo thể loại với phân trang
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);
    @Query("SELECT b FROM Book b JOIN b.author a JOIN b.category c WHERE "
            + "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);
}

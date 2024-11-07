package com.example.QLThuVien.repository;
import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // Thêm phương thức để tìm các bản ghi mượn theo người dùng
    List<BorrowRecord> findByUser (User user);
    List<BorrowRecord> findByIsReturnedFalse();
}


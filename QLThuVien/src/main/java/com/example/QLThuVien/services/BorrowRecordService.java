package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.entity.ReturnRecord;
import com.example.QLThuVien.entity.User;
import com.example.QLThuVien.repository.BorrowRecordRepository;
import com.example.QLThuVien.repository.ReturnRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private ReturnRecordRepository returnRecordRepository;

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public void addBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecordRepository.save(borrowRecord);
    }

    public BorrowRecord getBorrowRecordById(Long id) {
        return borrowRecordRepository.findById(id).orElse(null);
    }

    public void updateBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecordRepository.save(borrowRecord);
    }
    public void save(ReturnRecord returnRecord) {
        returnRecordRepository.save(returnRecord); // Lưu bản ghi trả sách
    }

    public void deleteBorrowRecord(Long id) {
        borrowRecordRepository.deleteById(id);
    }
    public BorrowRecord save(BorrowRecord borrowRecord) {
        return borrowRecordRepository.save(borrowRecord);
    }
    public List<BorrowRecord> findByUser (User user) {
        // Lấy danh sách tất cả các bản ghi mượn của người dùng
        List<BorrowRecord> allRecords = borrowRecordRepository.findByUser (user);

        // Lọc ra những bản ghi chưa trả
        return allRecords.stream()
                .filter(borrowRecord -> !borrowRecord.isReturned())
                .collect(Collectors.toList());
    }

    public List<ReturnRecord> findReturnedRecordsByUser (User user) {
        return returnRecordRepository.findByUser (user); // Gọi phương thức trong ReturnRecordRepository
    }
}


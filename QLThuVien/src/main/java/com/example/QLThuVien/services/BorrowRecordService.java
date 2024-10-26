package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

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

    public void deleteBorrowRecord(Long id) {
        borrowRecordRepository.deleteById(id);
    }
}

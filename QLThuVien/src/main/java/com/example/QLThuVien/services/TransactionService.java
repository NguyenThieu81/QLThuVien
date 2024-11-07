package com.example.QLThuVien.services;

import com.example.QLThuVien.entity.BorrowRecord;
import com.example.QLThuVien.entity.ReturnRecord;
import com.example.QLThuVien.repository.BorrowRecordRepository;
import com.example.QLThuVien.repository.ReturnRecordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final ReturnRecordRepository returnRecordRepository;

    public TransactionService(BorrowRecordRepository borrowRecordRepository, ReturnRecordRepository returnRecordRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.returnRecordRepository = returnRecordRepository;
    }

    public BorrowRecord saveBorrowRecord(BorrowRecord borrowRecord) {
        return borrowRecordRepository.save(borrowRecord);
    }

    public ReturnRecord saveReturnRecord(ReturnRecord returnRecord) {
        return returnRecordRepository.save(returnRecord);
    }
    public List<Object[]> getTransactionSummary() {
        List<Object[]> summaryList = new ArrayList<>();

        // Lấy danh sách mượn
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findAll();
        for (BorrowRecord borrow : borrowRecords) {
            summaryList.add(new Object[]{
                    borrow.getUser ().getUsername(),
                    borrow.getBook().getTitle(),
                    borrow.getFormattedBorrowDate(),
                    "Mượn"
            });
        }

        // Lấy danh sách trả
        List<ReturnRecord> returnRecords = returnRecordRepository.findAll();
        for (ReturnRecord returnRecord : returnRecords) {
            summaryList.add(new Object[]{
                    returnRecord.getUser ().getUsername(),
                    returnRecord.getBook().getTitle(),
                    returnRecord.getFormattedReturnDate(),
                    "Trả"
            });
        }

        return summaryList;
    }
}

package com.example.QLThuVien.repository;

import com.example.QLThuVien.entity.ReturnRecord;
import com.example.QLThuVien.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long> {
    List<ReturnRecord> findByUser (User user);
}
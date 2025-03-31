package com.example.QLThuVien.services;
import com.example.QLThuVien.AppointmentStatus;
import com.example.QLThuVien.entity.Appointment;
import com.example.QLThuVien.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevenueService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Long getTotalRevenue() {
        List<Appointment> completedOrReturnedAppointments = appointmentRepository.findByStatusIn(AppointmentStatus.COMPLETED, AppointmentStatus.RETURNED);
        return completedOrReturnedAppointments.stream()
                .mapToLong(Appointment::getTotalCost)
                .sum();
    }
}

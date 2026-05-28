package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Integer> {
}

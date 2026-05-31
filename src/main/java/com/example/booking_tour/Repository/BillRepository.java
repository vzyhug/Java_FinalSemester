package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Bill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface BillRepository extends JpaRepository<Bill, Integer> {

    Page<Bill> findByBillNumberContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(b.finalAmount), 0)
        FROM Bill b
    """)
    BigDecimal getTotalRevenue();

    List<Bill> findByBookingCustomerCustomerIdOrderByBillDateDesc(Integer customerId);
    Bill findByBookingBookingId(Integer bookingId);
}

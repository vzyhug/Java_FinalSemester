package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Lấy danh sách payment theo ID của Booking
     */
    List<Payment> findByBooking_BookingId(Integer bookingId);

    /**
     * Lấy payment theo phương thức thanh toán
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    boolean existsByBooking_BookingId(Integer bookingId);

    List<Payment> findByBookingCustomerCustomerIdOrderByPaymentDateDesc(Integer customerId);
    Payment findByBookingBookingId(Integer bookingId);
}


package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServices {

    private final PaymentRepository paymentRepository;

    public List<Payment> getPaymentsByCustomer(Integer customerId) {
        return paymentRepository
                .findByBookingCustomerCustomerIdOrderByPaymentDateDesc(customerId);
    }

    public Payment getPaymentByBookingId(Integer bookingId) {
        return paymentRepository.findByBookingBookingId(bookingId);
    }

    public Page<Payment> findAll(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return paymentRepository.findAll(pageable);
    }
}

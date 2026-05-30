package com.example.booking_tour.Services;


import com.example.booking_tour.Model.Bill;
import com.example.booking_tour.Repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServices {

    private final BillRepository billRepository;

    public List<Bill> getBillsByCustomer(Integer customerId) {
        return billRepository.findByBookingCustomerCustomerIdOrderByBillDateDesc(customerId);
    }

    public Bill getBillByBookingId(Integer bookingId) {
        return billRepository.findByBookingBookingId(bookingId);
    }
}

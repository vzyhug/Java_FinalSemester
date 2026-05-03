package com.example.booking_tour.Services;

import org.springframework.stereotype.Service;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Service
@RequiredArgsConstructor
public class CutomerServices {
    private final CustomerRepository customerRepository;

    // Lưu thông tin khách hàng mới sau khi đăng ký
    @Transactional
    public void register(Customer customer) {
        // Lưu thông tin vào database
        customerRepository.save(customer);
    }
}

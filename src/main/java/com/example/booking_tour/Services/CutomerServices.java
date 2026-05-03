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

    // đăng nhập tài khoản khách hàng
    public Customer login(String email, String password) {
        // Tìm khách hàng trong database
        Customer existingCustomer = customerRepository.findByEmail(email);
        // Nếu tìm thấy khách hàng và mật khẩu khớp thì trả về đối tượng Customer
        if (existingCustomer != null && existingCustomer.getPasswordHash().equals(password)) {
            return existingCustomer;
        }
        return null; // Đăng nhập thất bại
    }
}

package com.example.booking_tour.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booking_tour.Model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // tìm khách hàng theo email
    Customer findByEmail(String email);

}

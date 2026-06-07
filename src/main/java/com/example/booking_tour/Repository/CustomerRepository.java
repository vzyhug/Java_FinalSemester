package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // tìm khách hàng theo email
    Customer findByEmail(String email);

    List<Customer> findByFullNameContaining(String fullName);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    long countCustomersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}

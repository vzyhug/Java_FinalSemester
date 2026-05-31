package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    /**
     * Tìm employee theo username
     */
    Employee findByUsername(String username);

    /**
     * Tìm employee theo email
     */
    Employee findByEmail(String email);
}


package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);
}

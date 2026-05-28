package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByFullNameContainingIgnoreCase(String keyword);
    List<Employee> findByEmailContainingIgnoreCase(String keyword);

    long countByRole_RoleId(Integer roleId);

    Page<Employee> findAll(Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Employee> findByRole_RoleId(Integer roleId, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCaseAndRole_RoleId(String keyword, Integer roleId, Pageable pageable);


}

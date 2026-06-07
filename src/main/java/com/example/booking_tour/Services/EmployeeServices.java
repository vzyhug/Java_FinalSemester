package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServices {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // lấy tất cả nhân viên
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }
    //thêm nhân viên mới
    public Employee saveEmployee(Employee employee){
        String raw = employee.getPasswordHash();
        if (raw != null && !raw.trim().isEmpty() && !CustomerServices.isAlreadyHashed(raw)) {
            employee.setPasswordHash(passwordEncoder.encode(raw));
        }
        return employeeRepository.save(employee);
    }
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employee.setIsActive(false);
            employeeRepository.save(employee);
        }
    }
    //tìm theo id
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id).orElse(null);
    }
    //tìm kiếm thông tin
    public List<Employee> searchEmployee(String keyword) {
        return employeeRepository.findByFullNameContainingIgnoreCase(keyword);
    }

    // lấy admin hiện tại (ví dụ mặc định)
    public Employee getCurrentAdmin() {
        // giả sử roleId = 1 là Admin
        return employeeRepository.findFirstByRole_RoleId(1)
                .orElse(null);
    }
}

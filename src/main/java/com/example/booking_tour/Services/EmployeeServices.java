package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServices {
    @Autowired
    private EmployeeRepository employeeRepository;

    // lấy tất cả nhân viên
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }
    //thêm nhân viên mới
    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }
    //xóa nhân viên
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
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

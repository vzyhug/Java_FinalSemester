package com.example.booking_tour.Config;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.EmployeeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public class GlobalModelAttributes {

    @Autowired
    private EmployeeServices employeeService;

    @ModelAttribute("admin")
    public Employee addAdminToModel() {
        // logic lấy admin từ session hoặc SecurityContext
        return employeeService.getCurrentAdmin();
    }
}

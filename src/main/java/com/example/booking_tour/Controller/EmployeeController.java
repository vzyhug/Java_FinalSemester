package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Role;
import com.example.booking_tour.Repository.EmployeeRepository;
import com.example.booking_tour.Repository.RoleRepository;
import com.example.booking_tour.Services.EmployeeServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeServices employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    // ================= DANH SÁCH =================
    @GetMapping
    public String listEmployees(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Integer roleId,

            Model model) {

        model.addAttribute("admin", employeeService.getCurrentAdmin());


        Page<Employee> employeePage;

        // SEARCH + FILTER
        if (keyword != null
                && !keyword.isEmpty()
                && roleId != null) {

            employeePage = employeeRepository.findByFullNameContainingIgnoreCaseAndRole_RoleId(keyword, roleId, PageRequest.of(page, 5));

        }

        // SEARCH ONLY
        else if (keyword != null
                && !keyword.isEmpty()) {

            employeePage = employeeRepository.findByFullNameContainingIgnoreCase(keyword, PageRequest.of(page, 5));

        }

        // FILTER ONLY
        else if (roleId != null) {

            employeePage = employeeRepository.findByRole_RoleId(roleId, PageRequest.of(page, 5));

        }
        // ALL
        else {

            employeePage = employeeRepository.findAll(PageRequest.of(page, 5));
        }

        // DATA
        model.addAttribute("employees", employeePage.getContent());

        model.addAttribute("currentPage", page);

        model.addAttribute("totalPages", employeePage.getTotalPages());

        model.addAttribute("totalItems", employeePage.getTotalElements());

        // SEARCH + FILTER VALUE
        model.addAttribute("keyword", keyword);

        model.addAttribute("selectedRoleId", roleId);

        // ROLES
        model.addAttribute("roles", roleRepository.findAll());

        // COUNT
        model.addAttribute("totalEmployees", employeeRepository.count());

        model.addAttribute("totalAdmins", employeeRepository.countByRole_RoleId(1));

        model.addAttribute("totalSales", employeeRepository.countByRole_RoleId(2));

        model.addAttribute("totalGuides", employeeRepository.countByRole_RoleId(3));

        // TITLE
        model.addAttribute("parentTitle",
                "Hệ thống");

        model.addAttribute("currentTitle",
                "Quản lý Nhân viên");

        model.addAttribute("pageDescription",
                "Quản lý đội ngũ và phân quyền truy cập hệ thống");

        model.addAttribute("showAddButton",
                true);

        return "admin_human_resource_management";
    }

    // ================= FORM ADD =================
    @GetMapping("/add")
    public String addEmployeeForm(Model model) {

        model.addAttribute("employee",
                new Employee());

        model.addAttribute("roles",
                roleRepository.findAll());

        // ================= TITLE =================

        model.addAttribute("parentTitle",
                "Quản lý Nhân viên");

        model.addAttribute("currentTitle",
                "Thêm nhân viên mới");

        model.addAttribute("pageDescription",
                "Tạo tài khoản nhân viên mới cho hệ thống");

        model.addAttribute("showAddButton",
                false);

        return "add_employee";
    }

    // ================= SAVE =================
    @PostMapping("/save")
    public String saveEmployee(
            @ModelAttribute Employee employee,
            @RequestParam("roleId") Integer roleId) {

        Role role =
                roleRepository.findById(roleId).orElse(null);

        employee.setRole(role);

        employeeService.saveEmployee(employee);

        return "redirect:/employees";
    }

    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id) {

        employeeService.deleteEmployee(id);

        return "redirect:/employees";
    }

    // ================= EDIT =================
    @GetMapping("/edit/{id}")
    public String editEmployee(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute("employee",
                employeeService.getEmployeeById(id));

        model.addAttribute("roles",
                roleRepository.findAll());

        // ================= TITLE =================

        model.addAttribute("parentTitle",
                "Quản lý Nhân viên");

        model.addAttribute("currentTitle",
                "Chỉnh sửa nhân viên");

        model.addAttribute("pageDescription",
                "Cập nhật thông tin nhân viên");

        model.addAttribute("showAddButton",
                false);

        return "edit_employee";
    }

    // ================= UPDATE =================
    @PostMapping("/update")
    public String updateEmployee(
            @ModelAttribute Employee employee,
            @RequestParam("roleId") Integer roleId) {

        Role role =
                roleRepository.findById(roleId).orElse(null);

        employee.setRole(role);

        employeeService.saveEmployee(employee);

        return "redirect:/employees";
    }
}
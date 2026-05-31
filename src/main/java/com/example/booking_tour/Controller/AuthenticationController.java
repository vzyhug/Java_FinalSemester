package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/auth") // Đổi từ /admin thành /auth
public class AuthenticationController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/loginForm")
    public String loginForm(Model model) {
        model.addAttribute("isAdmin", true);
        return "login_form";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        Employee employee = employeeRepository.findByUsername(username);

        if (employee != null && employee.getPasswordHash().equals(password) && employee.getIsActive()) {
            session.setAttribute("loggedInAdmin", employee);

            if (employee.getRole().getRoleName().equalsIgnoreCase("Admin")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            return "login_form";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/loginForm";
    }
}

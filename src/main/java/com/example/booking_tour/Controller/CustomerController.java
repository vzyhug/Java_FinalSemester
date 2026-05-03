package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booking_tour.Model.Customer;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    // Hiển thị trang đăng nhập cho khách hàng
    @GetMapping("loginForm")
    public String loginForm(Model model) {
        // hiển thị form đăng nhập
        model.addAttribute("customer", new Customer());
        return "login_form"; // Trả về tên của view (customer_login.html)
    }

    // Hiển thị trang đăng ký cho khách hàng
    @GetMapping("registerForm")
    public String registerForm(Model model) {
        // hiển thị form đăng ký
        model.addAttribute("customer", new Customer());
        return "register_form"; // Trả về tên của view (customer_register.html)
    }
}

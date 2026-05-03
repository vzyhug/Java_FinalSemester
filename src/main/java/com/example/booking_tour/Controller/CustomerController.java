package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Services.CutomerServices;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CutomerServices customerServices;

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
        // hiển thị form đăng ky
        model.addAttribute("customer", new Customer());
        return "register_form"; // Trả về tên của view (customer_register.html)
    }

    // đăng ký tài khoản khách hàng
    @PostMapping("register")
    public String register(@Valid @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register_form"; // Trả lại form nếu có lỗi
        }
        // Lưu thông tin vào database
        customerServices.register(customer);
        return "redirect:/customer/loginForm";
    }
}

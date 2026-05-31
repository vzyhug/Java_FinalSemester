package com.example.booking_tour.Controller;


import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.BookingPassenger;
import com.example.booking_tour.Services.BookingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.CustomerServices;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    @Autowired
    private final CustomerServices customerServices;

    @Autowired
    private final BookingServices bookingServices;

    // Hiển thị trang đăng nhập cho khách hàng
    @GetMapping("loginForm")
    public String loginForm(Model model) {
        // hiển thị form đăng nhập
        model.addAttribute("customer", new Customer());
        return "login_form"; // Trả về tên của view (customer_login.html)
    }

    // Đăng nhập tài khoản khách hàng
    @PostMapping("login")
    public String login(@RequestParam("email") String email, 
                        @RequestParam("password") String password, 
                        HttpSession session, 
                        Model model) {
        Customer loggedInCustomer = customerServices.login(email, password);
        if (loggedInCustomer != null) {
            session.setAttribute("loggedInCustomer", loggedInCustomer);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
            return "login_form";
        }
    }

    // Đăng xuất
    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInCustomer");
        return "redirect:/";
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

    // Trang quản lý khách hàng
    @GetMapping
    public String customerManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            Long totalCustomers = customerServices.getTotalCustomers();
            Long newCustomers = customerServices.getNewCustomersThisMonth();
            Double returnRate = customerServices.getReturnRate();
            Double avgRating = customerServices.getAverageRating();
            List<Customer> customers = customerServices.getAllCustomers();

            model.addAttribute("totalCustomers", totalCustomers);
            model.addAttribute("newCustomersThisMonth", customerServices.getNewCustomersThisMonth());
            model.addAttribute("returnRate", customerServices.getReturnRate());
            model.addAttribute("avgRating", avgRating);
            model.addAttribute("customers", customers);
            model.addAttribute("admin", loggedInAdmin);

            return "admin_customer_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_customer_management";
        }
    }

    // Tìm kiếm khách hàng
    @GetMapping("/search")
    public String searchCustomers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            List<Customer> results = customerServices.searchCustomersByName(keyword);

            model.addAttribute("customers", results);
            model.addAttribute("keyword", keyword);
            model.addAttribute("totalCustomers", customerServices.getTotalCustomers());
            model.addAttribute("newCustomers", customerServices.getNewCustomersThisMonth());
            Double returnRate = customerServices.getReturnRate();
            model.addAttribute("avgRating", customerServices.getAverageRating());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_customer_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tìm kiếm: " + e.getMessage());
            return "admin_customer_management";
        }
    }

    // Chi tiết khách hàng
    @GetMapping("/{id}")
    public String getCustomerDetail(
            @PathVariable(value = "id") Integer customerId,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            Customer customer = customerServices.getCustomerById(customerId);

            if (customer == null) {
                model.addAttribute("error", "Khách hàng không tồn tại!");
                return "admin_customer_management";
            }

            Long tourCount = customerServices.getCustomerTourCount(customerId);
            java.math.BigDecimal totalSpent = customerServices.getCustomerTotalSpent(customerId);

            model.addAttribute("customer", customer);
            model.addAttribute("tourCount", tourCount);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("admin", loggedInAdmin);

            return "admin_customer_detail";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_customer_management";
        }
    }

    @GetMapping("booking")
    public String customerBookingHistory(Model model, HttpSession session)
    {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        List<Booking> listBookingOfCustomer=bookingServices.getBookingByPassenger(customer);
        model.addAttribute("listBooking", listBookingOfCustomer);
        return "my_tour";
    }

    @GetMapping("/booking/{idBooking}")
    public String customerBookingHistory(Model model, @PathVariable("idBooking") int idBooking, HttpSession session)
    {
        Booking booking=bookingServices.getBookingById(idBooking);
        List<BookingPassenger> listPassenger=bookingServices.getPassengerByBookingId(idBooking);
        model.addAttribute("listPassenger", listPassenger);
        model.addAttribute("booking", booking);
        return "my_tour_detail";

    }
}

package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Bill;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Services.BillServices;
import com.example.booking_tour.Services.CustomerServices;
import com.example.booking_tour.Services.PaymentServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor

public class CustomerController {
    private final CustomerServices customerService;

    private final PaymentServices paymentService;
    private final BillServices billService;
    // Hiển thị trang đăng nhập cho khách hàng
    @GetMapping("loginForm")
    public String loginForm(Model model) {
        // hiển thị form đăng nhập
        model.addAttribute("customer", new Customer());
        model.addAttribute("isAdmin", false);
        return "login_form"; // Trả về tên của view (customer_login.html)
    }

    // Đăng nhập tài khoản khách hàng
    @PostMapping("login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        Customer loggedInCustomer = customerService.login(email, password);

        if (loggedInCustomer != null) {

            session.setAttribute("loggedInCustomer", loggedInCustomer);
            return "redirect:/";

        } else {

            model.addAttribute("error",
                    "Email hoặc mật khẩu không chính xác!");

            model.addAttribute("isAdmin", false);

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
        customerService.register(customer);
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
            Long totalCustomers = customerService.getTotalCustomers();
            Long newCustomers = customerService.getNewCustomersThisMonth();
            Double returnRate = customerService.getReturnRate();
            Double avgRating = customerService.getAverageRating();
            List<Customer> customers = customerService.getAllCustomers();

            model.addAttribute("totalCustomers", totalCustomers);
            model.addAttribute("newCustomersThisMonth", customerService.getNewCustomersThisMonth());
            model.addAttribute("returnRate", customerService.getReturnRate());
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
            List<Customer> results = customerService.searchCustomersByName(keyword);

            model.addAttribute("customers", results);
            model.addAttribute("keyword", keyword);
            model.addAttribute("totalCustomers", customerService.getTotalCustomers());
            model.addAttribute("newCustomers", customerService.getNewCustomersThisMonth());
            Double returnRate = customerService.getReturnRate();
            model.addAttribute("avgRating", customerService.getAverageRating());
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
            Customer customer = customerService.getCustomerById(customerId);

            if (customer == null) {
                model.addAttribute("error", "Khách hàng không tồn tại!");
                return "admin_customer_management";
            }

            Long tourCount = customerService.getCustomerTourCount(customerId);
            java.math.BigDecimal totalSpent = customerService.getCustomerTotalSpent(customerId);

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
    @PostMapping("/toggle-status")
    public String toggleCustomerStatus(
            @RequestParam("customerId") Integer customerId,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        System.out.println("===== NHẬN REQUEST KHÓA/MỞ KHÓA TỪ GIAO DIỆN =====");
        System.out.println("Customer ID nhận được là: " + customerId);

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            customerService.toggleCustomerStatus(customerId);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            // IN LỖI ĐỎ RA CONSOLE ĐỂ BẮT BỆNH
            System.err.println("LỖI RỒI: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
        }

        return "redirect:/customer";
    }

    //Profile
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        Customer loggedInCustomer =
                (Customer) session.getAttribute("loggedInCustomer");

        if (loggedInCustomer == null) {
            return "redirect:/customer/loginForm";
        }

        // lấy dữ liệu mới nhất từ DB
        Customer customer =
                customerService.getCustomerById(loggedInCustomer.getCustomerId());

        model.addAttribute("customer", customer);

        return "personal_profile";
    }

    /// Change password profile
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        Customer loggedInCustomer =
                (Customer) session.getAttribute("loggedInCustomer");

        if (loggedInCustomer == null) {
            return "redirect:/customer/loginForm";
        }

        Customer customer =
                customerService.getCustomerById(loggedInCustomer.getCustomerId());

        // kiểm tra mật khẩu cũ
        if (!customer.getPasswordHash().equals(currentPassword)) {

            model.addAttribute("customer", customer);
            model.addAttribute("error", "Mật khẩu hiện tại không đúng!");

            return "customer_profile";
        }

        // kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute("customer", customer);
            model.addAttribute("error", "Xác nhận mật khẩu không khớp!");

            return "customer_profile";
        }

        // kiểm tra độ dài
        if (newPassword.length() < 6) {

            model.addAttribute("customer", customer);
            model.addAttribute("error", "Mật khẩu phải ít nhất 6 ký tự!");

            return "customer_profile";
        }

        customerService.changePassword(customer.getCustomerId(), newPassword);

        model.addAttribute("customer", customer);
        model.addAttribute("message", "Đổi mật khẩu thành công!");

        return "personal_profile";
    }


    //update profile
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Customer formCustomer,
                                HttpSession session,
                                Model model) {

        Customer loggedInCustomer =
                (Customer) session.getAttribute("loggedInCustomer");

        if (loggedInCustomer == null) {
            return "redirect:/customer/loginForm";
        }

        // lấy dữ liệu cũ từ DB
        Customer customer =
                customerService.getCustomerById(
                        loggedInCustomer.getCustomerId());

        // chỉ update field được sửa
        if(formCustomer.getFullName() != null)
            customer.setFullName(formCustomer.getFullName());

        if(formCustomer.getEmail() != null)
            customer.setEmail(formCustomer.getEmail());

        if(formCustomer.getPhone() != null)
            customer.setPhone(formCustomer.getPhone());

        if(formCustomer.getAddress() != null)
            customer.setAddress(formCustomer.getAddress());

        // save object đầy đủ
        customerService.updateCustomer(customer);

        session.setAttribute("loggedInCustomer", customer);

        model.addAttribute("customer", customer);
        model.addAttribute("message", "Cập nhật thành công!");

        return "personal_profile";
    }
    //payment history
    @GetMapping("/payment-history")
    public String paymentHistory(HttpSession session,
                                 Model model) {

        Customer loggedInCustomer =
                (Customer) session.getAttribute("loggedInCustomer");

        if (loggedInCustomer == null) {
            return "redirect:/customer/loginForm";
        }

        Integer customerId = loggedInCustomer.getCustomerId();

        List<Payment> payments =
                paymentService.getPaymentsByCustomer(customerId);

        List<Bill> bills =
                billService.getBillsByCustomer(customerId);

        // Tính tổng các giao dịch SUCCESS
        double totalAmount = payments.stream()
                .filter(p -> p.getNotes() == null || "SUCCESS".equalsIgnoreCase(p.getNotes()))
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        model.addAttribute("payments", payments);
        model.addAttribute("bills", bills);

        // THÊM DÒNG NÀY
        model.addAttribute("totalAmount", totalAmount);

        return "payment_history";
    }
}

package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.CustomerServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerServices customerService;
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
        Customer loggedInCustomer = customerService.login(email, password);
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
        customerService.register(customer);
        return "redirect:/customer/loginForm";
    }


    // Trang quản lý khách hàng
    @GetMapping
    public String customerManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
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
            return "redirect:/auth/loginForm";
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
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }

        try {
            Customer customer = customerService.getCustomerById(customerId);

            // Xử lý khi không tìm thấy khách hàng: Trả về danh sách kèm thông báo lỗi
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại hoặc đã bị xóa!");
                return "redirect:/customer";
            }

            Long tourCount = customerService.getCustomerTourCount(customerId);
            java.math.BigDecimal totalSpent = customerService.getCustomerTotalSpent(customerId);

            // Lấy lịch sử các tour khách đã đặt
            model.addAttribute("bookings", customerService.getCustomerBookings(customerId));

            model.addAttribute("customer", customer);
            model.addAttribute("tourCount", tourCount);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("admin", loggedInAdmin);

            return "admin_customer_detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/customer";
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
            return "redirect:/auth/loginForm";
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


    // ==========================================
    // 3. THÊM KHÁCH HÀNG MỚI (Từ phía Admin)
    // ==========================================
    @GetMapping("/add")
    public String showAddCustomerForm(Model model, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        model.addAttribute("customer", new Customer());
        return "add_customer";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") Customer customer, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            // Cấp mật khẩu mặc định cho khách được Admin tạo tay
            customer.setPasswordHash("123456");
            customer.setIsActive(true);

            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("message", "Thêm khách hàng mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Email có thể đã tồn tại trong hệ thống.");
        }
        return "redirect:/customer";
    }

    // ==========================================
    // SỬA THÔNG TIN KHÁCH HÀNG
    // ==========================================
    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return "redirect:/customer";
        }
        model.addAttribute("customer", customer);
        return "edit_customer";
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute("customer") Customer customer, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            // Rất quan trọng: Lấy dữ liệu cũ để không làm mất mật khẩu và ngày tạo
            Customer oldCustomer = customerService.getCustomerById(customer.getCustomerId());
            if (oldCustomer != null) {
                customer.setPasswordHash(oldCustomer.getPasswordHash());
                customer.setCreatedAt(oldCustomer.getCreatedAt());
                customer.setIsActive(oldCustomer.getIsActive());

                customerService.saveCustomer(customer);
                redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin khách hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/customer";
    }
}

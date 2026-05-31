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

import com.example.booking_tour.Model.Bill;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Services.BillServices;
import com.example.booking_tour.Services.PaymentServices;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    @Autowired
    private final CustomerServices customerServices;

    @Autowired
    private final BookingServices bookingServices;


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

        Customer loggedInCustomer = customerServices.login(email, password);

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
        return "redirect:/home";
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
            return "redirect:/auth/loginForm";
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


            model.addAttribute("totalCustomers", totalCustomers);
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
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }

        try {

            Customer customer = customerServices.getCustomerById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại hoặc đã bị xóa!");
                return "redirect:/customer";
            }

            Long tourCount = customerServices.getCustomerTourCount(customerId);
            java.math.BigDecimal totalSpent = customerServices.getCustomerTotalSpent(customerId);


            // Lấy lịch sử các tour khách đã đặt
            model.addAttribute("bookings", customerServices.getCustomerBookings(customerId));

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

    @GetMapping("booking")
    public String customerBookingHistory(Model model, HttpSession session)
    {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        List<Booking> listBookingOfCustomer=bookingServices.getBookingByPassenger(customer);
        model.addAttribute("listBooking", listBookingOfCustomer);
        return "my_tour";
    }

    @GetMapping("/booking/{idBooking}")
    public String customerBookingHistory(Model model, @PathVariable("idBooking") int idBooking, HttpSession session) {
        Booking booking = bookingServices.getBookingById(idBooking);
        List<BookingPassenger> listPassenger = bookingServices.getPassengerByBookingId(idBooking);
        model.addAttribute("listPassenger", listPassenger);
        model.addAttribute("booking", booking);
        return "my_tour_detail";
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
            customerServices.toggleCustomerStatus(customerId);

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
                customerServices.getCustomerById(loggedInCustomer.getCustomerId());

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
                customerServices.getCustomerById(loggedInCustomer.getCustomerId());

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

        customerServices.changePassword(customer.getCustomerId(), newPassword);

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
                customerServices.getCustomerById(
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
        customerServices.updateCustomer(customer);

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

            customerServices.saveCustomer(customer);
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

        Customer customer = customerServices.getCustomerById(id);
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
            Customer oldCustomer = customerServices.getCustomerById(customer.getCustomerId());
            if (oldCustomer != null) {
                customer.setPasswordHash(oldCustomer.getPasswordHash());
                customer.setCreatedAt(oldCustomer.getCreatedAt());
                customer.setIsActive(oldCustomer.getIsActive());

                customerServices.saveCustomer(customer);
                redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin khách hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/customer";
    }
}

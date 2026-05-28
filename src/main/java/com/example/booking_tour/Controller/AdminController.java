package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    // ==================== DASHBOARD ====================

    /**
     * Hiển thị form đăng nhập admin
     */
    @GetMapping("/loginForm")
    public String loginForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("isAdmin", true);
        return "login_form";
    }

    /**
     * Xử lý đăng nhập admin
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Employee admin = adminService.adminLogin(username, password);

        if (admin != null) {
            session.setAttribute("loggedInAdmin", admin);
            return "redirect:/admin/dashboard"; // Đăng nhập đúng -> Vào thẳng Dashboard
        } else {
            // ĐĂNG NHẬP SAI: Thông báo lỗi ra màn hình
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");


            model.addAttribute("employee", new Employee());
            model.addAttribute("isAdmin", true);

            return "login_form";
        }
    }

    /**
     * Hiển thị trang dashboard chính
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Kiểm tra admin đã đăng nhập hay chưa
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            // Lấy dữ liệu thống kê
            model.addAttribute("totalRevenue", adminService.getTotalRevenue());
            model.addAttribute("totalBookings", adminService.getTotalBookings());
            model.addAttribute("totalCustomers", adminService.getTotalCustomers());
            model.addAttribute("totalTours", adminService.getTotalTours());

            // Lấy danh sách booking gần đây
            model.addAttribute("recentBookings", adminService.getRecentBookings(5));

            // Lấy booking đang chờ
            model.addAttribute("pendingBookings", adminService.getPendingBookings());

            // Thông tin admin
            model.addAttribute("admin", loggedInAdmin);

            return "admin_dashboard_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dashboard: " + e.getMessage());
            return "admin_dashboard_management";
        }
    }

    /**
     * Đăng xuất admin
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInAdmin");
        return "redirect:/home";
    }

    // ==================== NAVIGATION ====================

    /**
     * Redirect tới trang quản lý tour
     */
    @GetMapping("/tours")
    public String redirectToTours() {
        return "redirect:/tour/manager"; // Trỏ về đúng RequestMapping của Tour Controller
    }

    @GetMapping("/customers")
    public String redirectToCustomers() {
        return "redirect:/customer"; // Trỏ về đúng RequestMapping của Customer Controller
    }

    /**
     * Redirect tới trang quản lý chuyến đi
     */
//    @GetMapping("/departures")
//    public String redirectToDepartures() {
//        return "redirect:/admin/departures";
//    }
}

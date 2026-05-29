package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.AdminServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminServices adminService;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            HttpSession session, Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        java.time.LocalDate startDate = null;
        java.time.LocalDate endDate = null;

        // Trả lại đúng chuỗi người dùng vừa nhập để giao diện không bị mất dữ liệu
        model.addAttribute("selectedStartDate", startDateStr);
        model.addAttribute("selectedEndDate", endDateStr);

        try {
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                startDate = java.time.LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                endDate = java.time.LocalDate.parse(endDateStr);
            }

            // CHẶN NGAY NẾU NGÀY ĐI > NGÀY VỀ
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                model.addAttribute("error", "Lỗi: Ngày bắt đầu không thể lớn hơn ngày kết thúc!");
                // Hủy biến startDate và endDate để truy vấn DB không bị lỗi, lấy All-time tạm
                startDate = null;
                endDate = null;
            }
        } catch (Exception e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ!");
            startDate = null;
            endDate = null;
        }

        // Quy đổi ra LocalDateTime để gọi Service
        java.time.LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : java.time.LocalDateTime.of(2000, 1, 1, 0, 0);
        java.time.LocalDateTime end = (endDate != null) ? endDate.atTime(java.time.LocalTime.MAX) : java.time.LocalDateTime.now();

        // Gửi dữ liệu ra giao diện
        model.addAttribute("totalRevenue", adminService.getTotalRevenueByDateRange(start, end));
        model.addAttribute("totalBookings", adminService.getTotalBookingsByDateRange(start, end));
        model.addAttribute("totalCustomers", adminService.getTotalCustomersByDateRange(start, end));
        model.addAttribute("totalTours", adminService.getTotalTours());
        model.addAttribute("recentBookings", adminService.getRecentBookings(5));
        model.addAttribute("pendingBookings", adminService.getPendingBookings());
        model.addAttribute("admin", loggedInAdmin);

        return "admin_dashboard_management";
    }
    @GetMapping("/tours")
    public String redirectToTours() { return "redirect:/tour/manager"; }

}
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
    public String dashboard(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm"; // Trỏ về đường dẫn mới

        model.addAttribute("totalRevenue", adminService.getTotalRevenue());
        model.addAttribute("totalBookings", adminService.getTotalBookings());
        model.addAttribute("totalCustomers", adminService.getTotalCustomers());
        model.addAttribute("totalTours", adminService.getTotalTours());
        model.addAttribute("recentBookings", adminService.getRecentBookings(5));
        model.addAttribute("pendingBookings", adminService.getPendingBookings());
        model.addAttribute("admin", loggedInAdmin);

        return "admin_dashboard_management";
    }

    @GetMapping("/tours")
    public String redirectToTours() { return "redirect:/tour/manager"; }


}
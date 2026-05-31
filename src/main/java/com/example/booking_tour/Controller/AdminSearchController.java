package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Services.CustomerServices;
import com.example.booking_tour.Services.TourServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminSearchController {

    @Autowired
    private TourServices tourService;


    @Autowired
    private CustomerServices customerService;

    @GetMapping("/admin/search")
    public String globalSearch(
            @RequestParam(value = "globalKeyword", required = false) String keyword,
            HttpSession session,
            Model model) {

        // 1. Giữ lại trạng thái đăng nhập của Admin ngoài Layout
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }
        model.addAttribute("admin", loggedInAdmin);

        // 2. Xử lý tìm kiếm thực tế dưới DB và thảy ngược lại cho giao diện kết quả
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("searchedTours", tourService.searchTours(keyword));
            model.addAttribute("searchedCustomers", customerService.searchCustomersByName(keyword));
            model.addAttribute("keyword", keyword);
        }

        // Trả về file giao diện hiển thị kết quả tìm kiếm chung
        return "admin_search_results";
    }
}
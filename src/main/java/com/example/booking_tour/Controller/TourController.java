package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Services.TourService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourService tourService;
    @GetMapping("")
    public String tour(Model model) {
        model.addAttribute("message", "Welcome to the Tour Page!");
        return "list_tour"; // Trả về tên của view (list_tour.html)
    }


    // ==================== TOUR MANAGEMENT PAGE ====================
    @GetMapping("/manager")
    public String tourManagement(HttpSession session, Model model) {
        // Kiểm tra admin đã đăng nhập hay chưa
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            // Lấy stats từ TourService
            Long totalTours = tourService.getTotalTours();           // Tổng tour
            Long activeTours = tourService.getActiveTours();         // Tour hoạt động
            java.math.BigDecimal expectedRevenue = tourService.getExpectedRevenue(); // Doanh thu

            // Lấy danh sách tour
            List<Tour> tours = tourService.getAllTours();

            // Truyền dữ liệu vào Model để HTML có thể truy cập
            // Trong HTML: th:text="${totalTours}" để hiển thị
            model.addAttribute("totalTours", totalTours);
            model.addAttribute("activeTours", activeTours);
            model.addAttribute("expectedRevenue", expectedRevenue);
            model.addAttribute("tours", tours);
            model.addAttribute("admin", loggedInAdmin);

            return "admin_tour_management"; // Đảm bảo file nằm ở: src/main/resources/templates/admin/tour_management.html
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            return "admin_tour_management";
        }
    }

    // ==================== SEARCH & FILTER ====================

    @GetMapping("/search")
    public String searchTours(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            List<Tour> searchResults = tourService.searchTours(keyword);

            model.addAttribute("tours", searchResults);
            model.addAttribute("keyword", keyword);
            model.addAttribute("totalTours", tourService.getTotalTours());
            model.addAttribute("activeTours", tourService.getActiveTours());
            model.addAttribute("expectedRevenue", tourService.getExpectedRevenue());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_tour_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tìm kiếm: " + e.getMessage());
            return "admin_tour_management";
        }
    }

    // ==================== TOUR DETAIL ====================

    @GetMapping("/{id}")
    public String getTourDetail(
            @PathVariable(value = "id") Integer tourId,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            Tour tour = tourService.getTourById(tourId);

            if (tour == null) {
                model.addAttribute("error", "Tour không tồn tại!");
                return "admin_tour_management";
            }

            model.addAttribute("tour", tour);
            model.addAttribute("admin", loggedInAdmin);

            return "tour_detail";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_tour_management";
        }
    }

    // ==================== CREATE/UPDATE TOUR ====================

    @PostMapping("/save")
    public String saveTour(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("durationDays") Integer durationDays,
            @RequestParam("durationNights") Integer durationNights,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            Tour tour = new Tour();
            tour.setTitle(title);
            tour.setDescription(description);
            tour.setDurationDays(durationDays);
            tour.setDurationNights(durationNights);
            tour.setIsActive(true);

            Tour savedTour = tourService.saveTour(tour);

            if (savedTour != null) {
                // Lưu thành công, trả về trang quản lý
                return "redirect:/tour/manager?success=Tour đã được thêm thành công";
            } else {
                model.addAttribute("error", "Lỗi khi lưu tour");
                return "admin_tour_management";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_tour_management";
        }
    }

    // ==================== DELETE TOUR ====================

    @GetMapping("/delete/{id}")
    public String deleteTour(
            @PathVariable(value = "id") Integer tourId,
            HttpSession session) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            boolean success = tourService.deactivateTour(tourId);

            if (success) {
                return "redirect:/tour/manager?success=Tour đã được xóa";
            } else {
                return "redirect:/tour/manager?error=Lỗi khi xóa tour";
            }
        } catch (Exception e) {
            return "redirect:/tour/manager?error=" + e.getMessage();
        }
    }
}

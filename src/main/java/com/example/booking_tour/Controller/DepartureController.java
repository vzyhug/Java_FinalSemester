package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.DepartureService;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.DepartureServices;
import com.example.booking_tour.Services.TourServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/departures")
public class DepartureController {

    @Autowired
    private DepartureServices departureService;
    @Autowired
    private TourServices tourService;

    // Trang quản lý chuyến đi
    @GetMapping
    public String departureManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";
        try {
            // Lấy danh sách departures VÀ danh sách tours
            List<TourDeparture> departures = departureService.getAllDepartures();
            List<Tour> tours = tourService.getAllTours(); // CẦN CÓ SERVICE NÀY

            model.addAttribute("onGoingDepartures", departureService.getOnGoingDepartures());
            model.addAttribute("pendingGuideDepartures", departureService.getPendingGuideDepartures());
            model.addAttribute("todayPassengers", departureService.getTodayPassengers());
            model.addAttribute("upcomingMonth", departureService.getUpcomingDepartureMonth());

            // Truyền list rỗng nếu bị null để HTML không lỗi
            model.addAttribute("departures", departures != null ? departures : new java.util.ArrayList<>());
            model.addAttribute("tours", tours != null ? tours : new java.util.ArrayList<>());

            model.addAttribute("admin", loggedInAdmin);
            return "admin_trip_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_trip_management";
        }
    }

    // Chi tiết chuyến đi
    @GetMapping("/{id}")
    public String getDepartureDetail(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {

            return "redirect:/auth/loginForm"; // Sửa link redirect cho đúng chuẩn mới
        }

        try {
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure == null) {
//<<<<<<< HEAD
//                model.addAttribute("error", "Chuyến đi không tồn tại!");
//                return "admin_trip_management";
//            }
//
//            model.addAttribute("departure", departure);
//            model.addAttribute("admin", loggedInAdmin);
//
//            return "admin_departure_detail";
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi: " + e.getMessage());
//            return "admin_trip_management";
//        }
//    }
//
//    // Lọc chuyến đi theo status
//=======
                return "redirect:/admin/departures"; // Lỗi thì quay về trang danh sách cho an toàn
            }

            // Sửa chữ "departure" thành "dep" để HTML nhận diện được dữ liệu
            model.addAttribute("dep", departure);
            model.addAttribute("admin", loggedInAdmin);

            return "departure_detail"; // Trả về trang chi tiết HTML mới
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }

    // ==========================================
    // 2. LỌC CHUYẾN ĐI THEO TRẠNG THÁI
    // ==========================================
    @GetMapping("/filter")
    public String filterDepartures(
            @RequestParam(value = "status", required = false) String status,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
             return "redirect:/auth/loginForm";
        }

        try {
            List<TourDeparture> departures = null;

            if (status != null && !status.isEmpty()) {
                departures = departureService.getDeparturesByStatus(status);
            } else {
                departures = departureService.getAllDepartures();
            }

            model.addAttribute("departures", departures);
            // Bọc chống null cho danh sách
            model.addAttribute("departures", departures != null ? departures : new java.util.ArrayList<>());
            model.addAttribute("status", status);
            model.addAttribute("onGoingDepartures", departureService.getOnGoingDepartures());
            model.addAttribute("pendingGuideDepartures", departureService.getPendingGuideDepartures());
            model.addAttribute("todayPassengers", departureService.getTodayPassengers());
            model.addAttribute("upcomingMonth", departureService.getUpcomingDepartureMonth());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_trip_management";
        } catch (Exception e) {

            return "redirect:/admin/departures";
        }
    }

    // Hủy chuyến đi
    @GetMapping("/cancel/{id}")
    public String cancelDeparture(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session,
            RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes vào đây

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }
        try {
            boolean success = departureService.cancelDeparture(departureId);

            if (success) {
                // Spring sẽ tự động chuyển tiếng Việt thành định dạng URL an toàn
                redirectAttributes.addAttribute("success", "Chuyến đã bị hủy thành công");
                return "redirect:/admin/departures";
            } else {
                redirectAttributes.addAttribute("error", "Lỗi hủy chuyến");
                return "redirect:/admin/departures";
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/departures";
        }
    }
    // Hiển thị trang Thêm chuyến đi
    @GetMapping("/add")
    public String showAddDeparturePage(Model model) {
        model.addAttribute("departure", new TourDeparture());
        model.addAttribute("tours", tourService.getAllTours()); // Đảm bảo gọi đúng hàm trong TourServices
        return "add_departure"; // Tên file HTML mới
    }

}
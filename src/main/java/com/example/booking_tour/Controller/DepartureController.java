package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.AdminServices;
import com.example.booking_tour.Services.DepartureServices;
import com.example.booking_tour.Services.TourServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/departures")
public class DepartureController {

    @Autowired
    private DepartureServices departureService;
    @Autowired
    private TourServices tourService;
    @Autowired
    private AdminServices adminService;

    // ==========================================
    // TRANG QUẢN LÝ CHUYẾN ĐI
    // ==========================================
    @GetMapping
    public String departureManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            List<TourDeparture> departures = departureService.getAllDepartures();
            List<Tour> tours = tourService.getAllTours();

            model.addAttribute("onGoingDepartures", departureService.getOnGoingDepartures());
            model.addAttribute("pendingGuideDepartures", departureService.getPendingGuideDepartures());
            model.addAttribute("todayPassengers", departureService.getTodayPassengers());
            model.addAttribute("upcomingMonth", departureService.getUpcomingDepartureMonth());

            model.addAttribute("departures", departures != null ? departures : new java.util.ArrayList<>());
            model.addAttribute("tours", tours != null ? tours : new java.util.ArrayList<>());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_trip_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_trip_management";
        }
    }

    // ==========================================
    // HIỂN THỊ FORM THÊM CHUYẾN ĐI
    // ==========================================
    @GetMapping("/add")
    public String showAddDeparturePage(Model model) {
        model.addAttribute("departure", new TourDeparture());
        model.addAttribute("tours", tourService.getAllTours());
        return "add_departure";
    }

    // ==========================================
    // XỬ LÝ LƯU CHUYẾN ĐI MỚI (PHẦN BẠN BỊ THIẾU)
    // ==========================================
    @PostMapping("/save")
    public String saveDeparture(@ModelAttribute("departure") TourDeparture departure, RedirectAttributes redirectAttributes) {
        // Kiểm tra logic ngày tháng
        if (departure.getReturnDate() != null && departure.getDepartureDate() != null) {
            if (departure.getReturnDate().isBefore(departure.getDepartureDate())) {
                redirectAttributes.addAttribute("error", "Ngày trở về không thể diễn ra trước ngày khởi hành!");
                return "redirect:/admin/departures/add";
            }
        }

        try {
            // Khi mới tạo chuyến, Số chỗ trống = Tổng số chỗ
            departure.setAvailableSeats(departure.getMaxSeats());

            departureService.saveDeparture(departure);
            redirectAttributes.addAttribute("success", "Thêm chuyến đi mới thành công!");
            return "redirect:/admin/departures";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi lưu dữ liệu: " + e.getMessage());
            return "redirect:/admin/departures/add";
        }
    }

    // ==========================================
    // CHI TIẾT CHUYẾN ĐI (Đã thêm list Bookings)
    // ==========================================
    @GetMapping("/{id}")
    public String getDepartureDetail(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }

        try {
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure == null) {
                return "redirect:/admin/departures";
            }

            model.addAttribute("dep", departure);
            model.addAttribute("admin", loggedInAdmin);

            // --- Đã đưa lệnh lấy Booking vào đúng vị trí ---
            List<Booking> bookings = departureService.getDepartureBookings(departureId);
            model.addAttribute("bookings", bookings);

            return "departure_detail";
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }

    // ==========================================
    // LỌC CHUYẾN ĐI THEO TRẠNG THÁI
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

    // ==========================================
    // HỦY CHUYẾN ĐI (Đã nâng cấp an toàn nghiệp vụ)
    // ==========================================
    @GetMapping("/cancel/{id}")
    public String cancelDeparture(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }

        try {
            // 1. Lấy thông tin chuyến đi ra trước để kiểm tra
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure != null) {
                // 2. Tính số lượng khách đã đặt
                int bookedSeats = departure.getMaxSeats() - departure.getAvailableSeats();

                // 3. CHẶN TỪ GỐC: Nếu có khách thì không cho hủy
                if (bookedSeats > 0) {
                    redirectAttributes.addAttribute("error", "Không thể hủy! Chuyến này đang có " + bookedSeats + " vé đã được đặt. Vui lòng hủy vé/hoàn tiền cho khách trước.");
                    return "redirect:/admin/departures/" + departureId;
                }
            }

            // 4. Nếu an toàn (0 khách), tiến hành hủy mềm
            boolean success = departureService.cancelDeparture(departureId);

            if (success) {
                redirectAttributes.addAttribute("success", "Chuyến đi đã bị hủy thành công!");
            } else {
                redirectAttributes.addAttribute("error", "Lỗi: Không thể cập nhật trạng thái hủy.");
            }
            return "redirect:/admin/departures";

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/departures";
        }
    }
    // ==========================================
    // HIỂN THỊ FORM SỬA CHUYẾN ĐI
    // ==========================================
    @GetMapping("/edit/{id}")
    public String showEditDeparturePage(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            // Lấy thông tin chuyến đi cũ ra
            TourDeparture departure = departureService.getDepartureById(id);
            if (departure == null) {
                return "redirect:/admin/departures";
            }

            // Gửi dữ liệu qua bên HTML
            model.addAttribute("departure", departure);
            model.addAttribute("tours", tourService.getAllTours()); // Đổ lại danh sách Tour cho Dropdown

            return "edit_departure"; // Mở file HTML bạn vừa tạo lúc nãy
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }
    // ==========================================
    // XỬ LÝ LƯU CẬP NHẬT (UPDATE)
    // ==========================================
    @PostMapping("/update")
    public String updateDeparture(@ModelAttribute("departure") TourDeparture departure, RedirectAttributes redirectAttributes) {
        // 1. Kiểm tra logic ngày tháng (Ngày về không được trước ngày đi)
        if (departure.getReturnDate() != null && departure.getDepartureDate() != null) {
            if (departure.getReturnDate().isBefore(departure.getDepartureDate())) {
                redirectAttributes.addAttribute("error", "Ngày trở về không thể diễn ra trước ngày khởi hành!");
                return "redirect:/admin/departures/edit/" + departure.getDepartureId();
            }
        }

        try {
            // 2. Lấy dữ liệu cũ từ Database để đối chiếu
            TourDeparture oldDep = departureService.getDepartureById(departure.getDepartureId());

            if (oldDep != null) {
                // Tính toán thông minh: Giữ lại số vé đã bán, chỉ update ghế trống
                int bookedSeats = oldDep.getMaxSeats() - oldDep.getAvailableSeats(); // Số vé khách đã mua
                int newAvailable = departure.getMaxSeats() - bookedSeats; // Số ghế trống mới

                // Nếu Admin lỡ tay giảm tổng số ghế xuống thấp hơn số vé đã bán -> Báo lỗi ngay
                if (newAvailable < 0) {
                    redirectAttributes.addAttribute("error", "Lỗi: Tổng số chỗ mới không được nhỏ hơn số vé đã bán (" + bookedSeats + " vé)!");
                    return "redirect:/admin/departures/edit/" + departure.getDepartureId();
                }

                departure.setAvailableSeats(newAvailable);
                departure.setStatus(oldDep.getStatus()); // Giữ nguyên trạng thái cũ (Đang bán/Đã đầy)

                // Giữ nguyên HDV cũ nếu form không có chỗ sửa HDV
                departure.setGuide(oldDep.getGuide());
            }

            // 3. Lưu xuống Database (Vì đối tượng đã có ID nên JPA sẽ tự hiểu là lệnh UPDATE)
            departureService.saveDeparture(departure);

            redirectAttributes.addAttribute("success", "Cập nhật chuyến đi thành công!");
            return "redirect:/admin/departures/" + departure.getDepartureId(); // Trả về lại trang chi tiết

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            return "redirect:/admin/departures/edit/" + departure.getDepartureId();
        }
    }
    // ==========================================
    // 9. XỬ LÝ LƯU HDV VÀO CHUYẾN ĐI
    // ==========================================
    @PostMapping("/save-guide")
    public String saveGuideAssignment(
            @RequestParam("departureId") Integer departureId,
            @RequestParam("guideId") Integer guideId,
            RedirectAttributes redirectAttributes) {

        try {
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure != null) {
                // Dùng adminService để tìm nhân viên
                Employee guide = adminService.getAdminById(guideId);
                departure.setGuide(guide);

                departureService.saveDeparture(departure);
                redirectAttributes.addAttribute("success", "Đã gán Hướng dẫn viên thành công!");
            }
            return "redirect:/admin/departures/" + departureId;

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi gán HDV: " + e.getMessage());
            return "redirect:/admin/departures/" + departureId;
        }
    }
    // ==========================================
    // 10. PHỤC HỒI CHUYẾN ĐI (TỪ HỦY -> MỞ BÁN)
    // ==========================================
    @GetMapping("/restore/{id}")
    public String restoreDeparture(@PathVariable("id") Integer departureId, HttpSession session, RedirectAttributes redirectAttributes) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure != null && "cancelled".equals(departure.getStatus())) {
                // Logic thông minh: Kiểm tra xem còn chỗ không để set trạng thái chuẩn
                if (departure.getAvailableSeats() > 0) {
                    departure.setStatus("open"); // Còn chỗ thì mở bán lại
                } else {
                    departure.setStatus("full"); // Hết chỗ thì báo đầy
                }

                departureService.saveDeparture(departure); // Lưu xuống DB
                redirectAttributes.addAttribute("success", "Đã phục hồi chuyến đi thành công!");
            }
            return "redirect:/admin/departures/" + departureId;

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi phục hồi: " + e.getMessage());
            return "redirect:/admin/departures/" + departureId;
        }
    }
    // ==========================================
    // 8. HIỂN THỊ TRANG GÁN HDV
    // ==========================================
    @GetMapping("/assign-guide/{id}")
    public String showAssignGuidePage(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            TourDeparture departure = departureService.getDepartureById(id);
            if (departure == null) return "redirect:/admin/departures";

            model.addAttribute("departure", departure);

            // Đổ danh sách Hướng dẫn viên ra để chọn
            model.addAttribute("guides", adminService.getAllAdmins());

            return "assign_guide";
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }
}
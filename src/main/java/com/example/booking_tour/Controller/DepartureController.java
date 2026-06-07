package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.BookingPassenger;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private com.example.booking_tour.Services.BookingServices bookingServices;

// ==========================================
    // TRANG QUẢN LÝ CHUYẾN ĐI (TÌM KIẾM + LỌC THÁNG)
    // ==========================================
    @GetMapping
    public String departureManagement(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(value = "filterMonth", required = false) Integer filterMonth,
            @RequestParam(value = "filterYear", required = false) Integer filterYear,
            HttpSession session, Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            List<TourDeparture> departures;

            //Logic lọc dữ liệu (Ưu tiên lọc theo tháng/năm nếu người dùng click vào khối Thống kê)
            if (filterMonth != null && filterYear != null) {
                departures = departureService.getDeparturesByMonthAndYear(filterMonth, filterYear);
            }
            // Lọc theo form tìm kiếm Tên / Ngày cụ thể
            else if ((keyword != null && !keyword.trim().isEmpty()) || date != null) {
                departures = departureService.searchDepartures(keyword, date);
            }
            // Mặc định lấy tất cả
            else {
                departures = departureService.getAllDepartures();
            }

            // Tính toán ra cái tháng tới (Ví dụ: hiện tại đang tháng 5, tháng tới là tháng 6) để gắn vào link
            java.time.LocalDate nextMonthDate = java.time.LocalDate.now().plusMonths(1);
            model.addAttribute("nextMonthVal", nextMonthDate.getMonthValue());
            model.addAttribute("nextYearVal", nextMonthDate.getYear());

            List<Tour> tours = tourService.getAllTours();

            // Nạp thống kê
            model.addAttribute("onGoingDepartures", departureService.getOnGoingDepartures());
            model.addAttribute("pendingGuideDepartures", departureService.getPendingGuideDepartures());
            model.addAttribute("todayPassengers", departureService.getTodayPassengers());
            model.addAttribute("upcomingMonth", departureService.getUpcomingDepartureMonth());

            // Nạp dữ liệu
            model.addAttribute("departures", departures != null ? departures : new java.util.ArrayList<>());
            model.addAttribute("tours", tours != null ? tours : new java.util.ArrayList<>());
            model.addAttribute("admin", loggedInAdmin);

            // Giữ giá trị cho form tìm kiếm
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchDate", date);

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
    // XỬ LÝ LƯU CHUYẾN ĐI MỚI
    // ==========================================
    @PostMapping("/save")
    public String saveDeparture(
            @ModelAttribute("departure") TourDeparture departure,
            @RequestParam("tourId") Integer tourId,
            RedirectAttributes redirectAttributes) {

        // 1. NGÀY TRONG QUÁ KHỨ
        if (departure.getDepartureDate() != null && departure.getDepartureDate().isBefore(java.time.LocalDate.now())) {
            redirectAttributes.addAttribute("error", "Lỗi: Không thể tạo chuyến đi khởi hành trong quá khứ!");
            return "redirect:/admin/departures/add";
        }

        // 2. NGÀY ĐI - NGÀY VỀ
        if (departure.getReturnDate() != null && departure.getDepartureDate() != null) {
            if (departure.getReturnDate().isBefore(departure.getDepartureDate())) {
                redirectAttributes.addAttribute("error", "Ngày trở về không thể diễn ra trước ngày khởi hành!");
                return "redirect:/admin/departures/add";
            }
        }

        // 3. GIÁ TIỀN
        if (departure.getAdultPrice() != null && departure.getAdultPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addAttribute("error", "Lỗi: Giá người lớn không được là số âm!");
            return "redirect:/admin/departures/add";
        }
        if (departure.getChildPrice() != null && departure.getChildPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addAttribute("error", "Lỗi: Giá trẻ em không được là số âm!");
            return "redirect:/admin/departures/add";
        }
        if (departure.getAdultPrice() != null && departure.getChildPrice() != null) {
            if (departure.getChildPrice().compareTo(departure.getAdultPrice()) > 0) {
                redirectAttributes.addAttribute("error", "Lỗi: Giá trẻ em không được phép cao hơn giá người lớn!");
                return "redirect:/admin/departures/add";
            }
        }

        // 4. KIỂM TRA ĐỒNG BỘ SỐ NGÀY
        try {
            Tour originalTour = tourService.getTourById(tourId); // Chỉ dùng tourId lấy từ form

            if (originalTour != null) {
                departure.setTour(originalTour); // Gán tour vào để lưu DB không bị lỗi

                if (departure.getDepartureDate() != null && departure.getReturnDate() != null) {
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(departure.getDepartureDate(), departure.getReturnDate());
                    long actualDays = daysBetween + 1;

                    if (actualDays != originalTour.getDurationDays()) {
                        redirectAttributes.addAttribute("error", "Lỗi: Tour gốc quy định là "
                                + originalTour.getDurationDays() + " ngày, nhưng bạn lại chọn lịch đi " + actualDays + " ngày!");
                        return "redirect:/admin/departures/add";
                    }
                }
            } else {
                redirectAttributes.addAttribute("error", "Lỗi: Vui lòng chọn một Tour hợp lệ!");
                return "redirect:/admin/departures/add";
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/departures/add";
        }

        // 5. LƯU DỮ LIỆU
        try {
            departure.setAvailableSeats(departure.getMaxSeats());
            departureService.saveDeparture(departure);
            redirectAttributes.addAttribute("success", "Thêm chuyến đi mới thành công!");
            return "redirect:/admin/departures";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi lưu dữ liệu: " + e.getMessage());
            return "redirect:/admin/departures/add";
        }
    }    // ==========================================
    // CHI TIẾT CHUYẾN ĐI
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
            // Tự động đồng bộ hóa ghế trống dựa trên số vé booking thực tế để sửa dữ liệu cũ bị lệch
            bookingServices.updateAvailableSeats(departureId);

            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure == null) {
                return "redirect:/admin/departures";
            }

            model.addAttribute("dep", departure);
            model.addAttribute("admin", loggedInAdmin);

            // --- Đã đưa lệnh lấy Booking vào đúng vị trí ---
            List<Booking> bookings = departureService.getDepartureBookings(departureId);
            model.addAttribute("bookings", bookings);

            // Lấy danh sách hành khách thực tế của các booking hoạt động
            List<BookingPassenger> passengers = new java.util.ArrayList<>();
            if (bookings != null) {
                for (Booking b : bookings) {
                    if (!"cancelled".equalsIgnoreCase(b.getStatus())) {
                        List<BookingPassenger> bps = bookingServices.getPassengerByBookingId(b.getBookingId());
                        if (bps != null) {
                            passengers.addAll(bps);
                        }
                    }
                }
            }
            model.addAttribute("passengers", passengers);

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
            // BỔ SUNG THÊM IF NÀY ĐỂ XỬ LÝ LỌC HDV
            if ("no_guide".equals(status)) {
                departures = departureService.getPendingGuideDeparturesList();
            }
            else if (status != null && !status.isEmpty()) {
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

    // ==========================================
    // HỦY CHUYẾN ĐI
    // ==========================================
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
            //Lấy thông tin chuyến đi ra trước để kiểm tra
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure != null) {
                //Tính số lượng khách đã đặt
                int bookedSeats = departure.getMaxSeats() - departure.getAvailableSeats();

                // CHẶN TỪ GỐC: Nếu có khách thì không cho hủy
                if (bookedSeats > 0) {
                    redirectAttributes.addAttribute("error", "Không thể hủy! Chuyến này đang có " + bookedSeats + " vé đã được đặt. Vui lòng hủy vé/hoàn tiền cho khách trước.");
                    return "redirect:/admin/departures/" + departureId;
                }
            }

            // Nếu an toàn (0 khách), tiến hành hủy mềm
            boolean success = departureService.cancelDeparture(departureId);

            if (success) {
                // Spring sẽ tự động chuyển tiếng Việt thành định dạng URL an toàn
                redirectAttributes.addAttribute("success", "Chuyến đi đã bị hủy thành công!");
                return "redirect:/admin/departures";
            } else {
                redirectAttributes.addAttribute("error", "Lỗi: Không thể cập nhật trạng thái hủy.");
                return "redirect:/admin/departures";
            }
//            return "redirect:/admin/departures";

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/departures";
        }
    }

    // ==========================================
    // HOÀN THÀNH CHUYẾN ĐI (Cập nhật trạng thái completed)
    // ==========================================
    @GetMapping("/complete/{id}")
    public String completeDeparture(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/auth/loginForm";
        }
        try {
            TourDeparture departure = departureService.getDepartureById(departureId);
            if (departure == null) {
                redirectAttributes.addAttribute("error", "Lỗi: Không tìm thấy chuyến đi.");
                return "redirect:/admin/departures";
            }

            if ("cancelled".equalsIgnoreCase(departure.getStatus())) {
                redirectAttributes.addAttribute("error", "Lỗi: Chuyến đi đã bị hủy, không thể hoàn thành.");
                return "redirect:/admin/departures/" + departureId;
            }

            if ("completed".equalsIgnoreCase(departure.getStatus())) {
                redirectAttributes.addAttribute("error", "Chuyến đi này đã được đánh dấu hoàn thành trước đó.");
                return "redirect:/admin/departures/" + departureId;
            }

            boolean success = departureService.completeDeparture(departureId);

            if (success) {
                redirectAttributes.addAttribute("success", "Cập nhật trạng thái hoàn thành chuyến đi thành công!");
                return "redirect:/admin/departures/" + departureId;
            } else {
                redirectAttributes.addAttribute("error", "Lỗi: Không thể cập nhật trạng thái hoàn thành.");
                return "redirect:/admin/departures/" + departureId;
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Lỗi: " + e.getMessage());
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

            return "edit_departure";
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }
    // ==========================================
    // XỬ LÝ LƯU CẬP NHẬT (UPDATE)
    // ==========================================
    @PostMapping("/update")
    public String updateDeparture(@ModelAttribute("departure") TourDeparture departure, RedirectAttributes redirectAttributes) {
        //  Kiểm tra logic ngày tháng (Ngày về không được trước ngày đi)
        if (departure.getReturnDate() != null && departure.getDepartureDate() != null) {
            if (departure.getReturnDate().isBefore(departure.getDepartureDate())) {
                redirectAttributes.addAttribute("error", "Ngày trở về không thể diễn ra trước ngày khởi hành!");
                return "redirect:/admin/departures/edit/" + departure.getDepartureId();
            }
        }

        //  Kiểm tra giá tiền không âm và logic giá vé
        if (departure.getAdultPrice() != null && departure.getAdultPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addAttribute("error", "Lỗi: Giá người lớn không được là số âm!");
            return "redirect:/admin/departures/edit/" + departure.getDepartureId();
        }
        if (departure.getChildPrice() != null && departure.getChildPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addAttribute("error", "Lỗi: Giá trẻ em không được là số âm!");
            return "redirect:/admin/departures/edit/" + departure.getDepartureId();
        }
        if (departure.getAdultPrice() != null && departure.getChildPrice() != null) {
            if (departure.getChildPrice().compareTo(departure.getAdultPrice()) > 0) {
                redirectAttributes.addAttribute("error", "Lỗi: Giá trẻ em không được phép cao hơn giá người lớn!");
                return "redirect:/admin/departures/edit/" + departure.getDepartureId();
            }
        }

        try {
            // Lấy dữ liệu cũ từ Database để đối chiếu
            TourDeparture oldDep = departureService.getDepartureById(departure.getDepartureId());

            if (oldDep != null) {
                int bookedSeats = oldDep.getMaxSeats() - oldDep.getAvailableSeats(); // Số vé khách đã mua
                int newAvailable = departure.getMaxSeats() - bookedSeats; // Số ghế trống mới

                if (newAvailable < 0) {
                    redirectAttributes.addAttribute("error", "Lỗi: Tổng số chỗ mới không được nhỏ hơn số vé đã bán (" + bookedSeats + " vé)!");
                    return "redirect:/admin/departures/edit/" + departure.getDepartureId();
                }

                departure.setAvailableSeats(newAvailable);
                departure.setStatus(oldDep.getStatus()); // Giữ nguyên trạng thái cũ (Đang bán/Đã đầy)

                departure.setGuide(oldDep.getGuide());
            }

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
                java.time.LocalDate today = java.time.LocalDate.now();
                if (departure.getDepartureDate() != null && departure.getDepartureDate().isBefore(today)) {
                    redirectAttributes.addFlashAttribute("error", "Lỗi: Chuyến đi này đã qua ngày khởi hành (" + departure.getDepartureDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "), không thể phục hồi bán!");
                    return "redirect:/admin/departures/" + departureId;
                }

                if (departure.getAvailableSeats() > 0) {
                    departure.setStatus("open"); // Còn chỗ thì mở bán lại
                } else {
                    departure.setStatus("full"); // Hết chỗ thì báo đầy
                }

                departureService.saveDeparture(departure); // Lưu xuống DB
                redirectAttributes.addFlashAttribute("success", "Đã phục hồi chuyến đi thành công!");
            }
            return "redirect:/admin/departures/" + departureId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi phục hồi: " + e.getMessage());
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

            model.addAttribute("guides", adminService.getAllAdmins());

            return "assign_guide";
        } catch (Exception e) {
            return "redirect:/admin/departures";
        }
    }
}
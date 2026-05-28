package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.DepartureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/departures")
public class DepartureController {

    @Autowired
    private DepartureService departureService;

    // Trang quản lý chuyến đi
    @GetMapping
    public String departureManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            Long onGoing = departureService.getOnGoingDepartures();
            Long pendingGuide = departureService.getPendingGuideDepartures();
            Long todayPassengers = departureService.getTodayPassengers();
            String upcomingMonth = departureService.getUpcomingDepartureMonth();
            List<TourDeparture> departures = departureService.getAllDepartures();

            model.addAttribute("onGoingDepartures", onGoing);
            model.addAttribute("pendingGuideDepartures", pendingGuide);
            model.addAttribute("todayPassengers", todayPassengers);
            model.addAttribute("upcomingMonth", upcomingMonth);
            model.addAttribute("departures", departures);
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
            return "redirect:/admin/loginForm";
        }

        try {
            TourDeparture departure = departureService.getDepartureById(departureId);

            if (departure == null) {
                model.addAttribute("error", "Chuyến đi không tồn tại!");
                return "admin_trip_management";
            }

            model.addAttribute("departure", departure);
            model.addAttribute("admin", loggedInAdmin);

            return "admin_departure_detail";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin_trip_management";
        }
    }

    // Lọc chuyến đi theo status
    @GetMapping("/filter")
    public String filterDepartures(
            @RequestParam(value = "status", required = false) String status,
            HttpSession session,
            Model model) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            List<TourDeparture> departures = null;

            if (status != null && !status.isEmpty()) {
                departures = departureService.getDeparturesByStatus(status);
            } else {
                departures = departureService.getAllDepartures();
            }

            model.addAttribute("departures", departures);
            model.addAttribute("status", status);
            model.addAttribute("onGoingDepartures", departureService.getOnGoingDepartures());
            model.addAttribute("pendingGuideDepartures", departureService.getPendingGuideDepartures());
            model.addAttribute("todayPassengers", departureService.getTodayPassengers());
            model.addAttribute("upcomingMonth", departureService.getUpcomingDepartureMonth());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_trip_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi lọc: " + e.getMessage());
            return "admin_trip_management";
        }
    }

    // Hủy chuyến đi
    @GetMapping("/cancel/{id}")
    public String cancelDeparture(
            @PathVariable(value = "id") Integer departureId,
            HttpSession session) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/loginForm";
        }

        try {
            boolean success = departureService.cancelDeparture(departureId);

            if (success) {
                return "redirect:/admin/departures?success=Chuyến đã bị hủy";
            } else {
                return "redirect:/admin/departures?error=Lỗi hủy chuyến";
            }
        } catch (Exception e) {
            return "redirect:/admin/departures?error=" + e.getMessage();
        }
    }

}
package com.example.booking_tour.Controller;


import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Services.TourService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.booking_tour.Model.*;
import com.example.booking_tour.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import com.example.booking_tour.Model.*;
import com.example.booking_tour.Repository.TourRepository;
import com.example.booking_tour.Services.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tour")

@RequiredArgsConstructor
public class TourController {

    @Autowired
    private TourServices tourServices;
    private TourServices TServices;


    @Autowired
    private TourCategoryServices TCServices;

    @Autowired
    private TourScheduleServices TSCServices;

    @Autowired
    private ImagesTourServices IServices;

    @Autowired
    private ReviewServices RServices;

    @Autowired
    private TourDepartureServices TDServices;


    @Autowired
    private final TourRepository tourRepository;

    @GetMapping("")
    @Transactional(readOnly = true)
    public String list_tour(Model model, @RequestParam(value = "sort", required = false) String sort) {
        List<Tour> listTour;
        List<TourCategory> listTourCategory = TCServices.getAllTourCategories();


        if ("asc".equals(sort)) {
            listTour = tourServices.sortTourByPrice(true);
        } else if ("desc".equals(sort)) {
            listTour = tourServices.sortTourByPrice(false);
        } else {
            listTour = tourServices.getAllTours();
        }

        model.addAttribute("listCategory", listTourCategory);
        model.addAttribute("listTours", listTour);
        model.addAttribute("currentSort", sort);
        return "list_tour";
    }

    @GetMapping("region/{region}")
    @Transactional(readOnly = true)
    public String list_tour_region(Model model,@PathVariable(value = "region") String regionName) {
        List<Tour> listTour=tourServices.getAllTours();
        List<TourCategory> listTourCategory = TCServices.getAllTourCategories();
        List<Tour> tourRegion=new ArrayList<Tour>();
        for (Tour tour : listTour) {
            if(tour.getProvince().getRegion().name().equals(regionName))
            {
                tourRegion.add(tour);
            }
        }
        System.out.println("===========================================================");
        System.out.println("So luong lay ra duoc:"+tourRegion.size());
        model.addAttribute("listCategory", listTourCategory);
        model.addAttribute("listTours", tourRegion);
        return "list_tour";
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String tour_detail(Model model, @PathVariable(value = "id") Integer tourId) {
        // Danh sach tour
        List<Tour> listTour;

        // Tu danh sach tour lay ra thong tin tour: lich trinh, anh, review
        Tour tourDetail=tourServices.getToursByTourId(tourId);
        List<TourSchedule> listSchedule= TSCServices.getTourSchedulesByTour(tourDetail);
        List<ImagesTour> listImages= IServices.getAllImagesToursByTour(tourDetail);
        List<Review> listReview=RServices.getAllReviewByTour(tourDetail);
        List<TourDeparture> listDeparture=TDServices.getTourDepartureByTour(tourDetail);

        double startAverage= RServices.getStarAverage(listReview);

        model.addAttribute("tourDetail", tourDetail);
        model.addAttribute("listSchedule", listSchedule);
        model.addAttribute("listImages", listImages);
        model.addAttribute("listReview", listReview);
        model.addAttribute("startAverage", startAverage);
        model.addAttribute("totalReview", listReview.size());
        model.addAttribute("listDeparture", listDeparture);

        return "tour_detail";
    }


    @GetMapping("/check_date")
    public String checkExistDepartureDate(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            HttpServletRequest request, //Lấy thông tin header
            RedirectAttributes redirectAttributes) //Truyền message
    {
        List<TourDeparture> listTourD = TDServices.getAllTourDeparture();

        if (startDate != null)
        {
            for (TourDeparture tourD : listTourD) {
                if (tourD.getDepartureDate().equals(startDate)) {
                    return "redirect:/booking?departureId=" + tourD.getDepartureId();
                }
            }
            redirectAttributes.addFlashAttribute("message", "Rất tiếc chúng tôi không có đợt tour cho ngày khởi hành này :((");
        } else
        {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn ngày đi");
        }
        //Lấy url trang trước đó và quay lại
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;

    }


    // ==================== TOUR MANAGEMENT PAGE ====================

    @GetMapping("/manager")
    public String tourManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        try {
            model.addAttribute("totalTours", tourServices.getTotalTours());
            model.addAttribute("activeTours", tourServices.getActiveTours());
            model.addAttribute("expectedRevenue", tourServices.getExpectedRevenue());
            model.addAttribute("tours", tourServices.getAllTours());
            model.addAttribute("admin", loggedInAdmin);
            return "admin_tour_management";
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
            List<Tour> searchResults = tourServices.searchTours(keyword);

            model.addAttribute("tours", searchResults);
            model.addAttribute("keyword", keyword);
            model.addAttribute("totalTours", tourServices.getTotalTours());
            model.addAttribute("activeTours", tourServices.getActiveTours());
            model.addAttribute("expectedRevenue", tourServices.getExpectedRevenue());
            model.addAttribute("admin", loggedInAdmin);

            return "admin_tour_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tìm kiếm: " + e.getMessage());
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

            Tour savedTour = tourServices.saveTour(tour);

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
            boolean success = tourServices.deactivateTour(tourId);

            if (success) {
                return "redirect:/tour/manager?success=Tour đã được xóa";
            } else {
                return "redirect:/tour/manager?error=Lỗi khi xóa tour";
            }
        } catch (Exception e) {
            return "redirect:/tour/manager?error=" + e.getMessage();
        }
    }

    @GetMapping("/manager/detail/{id}")
    public String getTourDetail(@PathVariable(value = "id") Integer tourId, HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        Tour tour = tourServices.getTourById(tourId);
        if (tour == null) {
            model.addAttribute("error", "Tour không tồn tại!");
            return "admin_tour_management";
        }
        model.addAttribute("tour", tour);
        model.addAttribute("admin", loggedInAdmin);
        return "tour_detail";
    }




    //xem tour
    @GetMapping("/tours/by-province/{provinceId}")
    public String getToursByProvince(@PathVariable Integer provinceId, Model model) {
        List<Tour> tours = tourRepository.findByProvince_ProvinceId(provinceId);
        model.addAttribute("tours", tours);
        return "tours_by_province"; // view hiển thị danh sách tour
    }


}



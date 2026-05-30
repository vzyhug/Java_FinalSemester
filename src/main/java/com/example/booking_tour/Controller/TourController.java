package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.*;
import com.example.booking_tour.Repository.TourRepository;
import com.example.booking_tour.Services.*;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    public String list_tour_region(Model model, @PathVariable(value = "region") String regionName) {
        List<Tour> listTour = tourServices.getAllTours();
        List<TourCategory> listTourCategory = TCServices.getAllTourCategories();
        List<Tour> tourRegion = new ArrayList<>();
        for (Tour tour : listTour) {
            if (tour.getProvince().getRegion().name().equals(regionName)) {
                tourRegion.add(tour);
            }
        }
        System.out.println("===========================================================");
        System.out.println("So luong lay ra duoc:"+tourRegion.size());
        model.addAttribute("listCategory", listTourCategory);
        model.addAttribute("listTours", tourRegion);
        return "list_tour";
    }

    // ==================== CỦA KHÁCH HÀNG: Xem chi tiết ====================
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String tour_detail(Model model, @PathVariable(value = "id") Integer tourId) {
        Tour tourDetail = tourServices.getTourById(tourId);
        List<TourSchedule> listSchedule = TSCServices.getTourSchedulesByTour(tourDetail);
        List<ImagesTour> listImages = IServices.getAllImagesToursByTour(tourDetail);
        List<Review> listReview = RServices.getAllReviewByTour(tourDetail);

        double startAverage = RServices.getStarAverage(listReview);
        model.addAttribute("tourDetail", tourDetail);
        model.addAttribute("listSchedule", listSchedule);
        model.addAttribute("listImages", listImages);
        model.addAttribute("listReview", listReview);
        model.addAttribute("startAverage", startAverage);
        model.addAttribute("totalReview", listReview.size());

        return "tour_detail";
    }
//    public String list_tour_region(Model model,@PathVariable(value = "region") String regionName) {
//        List<Tour> listTour=TServices.getAllTours();
//        List<TourCategory> listTourCategory = TCServices.getAllTourCategories();
//        List<Tour> tourRegion=new ArrayList<Tour>();
//        for (Tour tour : listTour) {
//            if(tour.getProvince().getRegion().name().equals(regionName))
//            {
//                tourRegion.add(tour);
//            }
//        }
//        System.out.println("===========================================================");
//        System.out.println("So luong lay ra duoc:"+tourRegion.size());
//        model.addAttribute("listCategory", listTourCategory);
//        model.addAttribute("listTours", tourRegion);
//        return "list_tour";
//
//    }

//    @GetMapping("/{id}")
//    @Transactional(readOnly = true)
//    public String tour_detail(Model model, @PathVariable(value = "id") Integer tourId) {
//        // Danh sach tour
//        List<Tour> listTour;
//
//        // Tu danh sach tour lay ra thong tin tour: lich trinh, anh, review
//        Tour tourDetail=TServices.getToursByTourId(tourId);
//        List<TourSchedule> listSchedule= TSCServices.getTourSchedulesByTour(tourDetail);
//        List<ImagesTour> listImages= IServices.getAllImagesToursByTour(tourDetail);
//        List<Review> listReview=RServices.getAllReviewByTour(tourDetail);
//
//
//        double startAverage= RServices.getStarAverage(listReview);
//        model.addAttribute("tourDetail", tourDetail);
//        model.addAttribute("listSchedule", listSchedule);
//        model.addAttribute("listImages", listImages);
//        model.addAttribute("listReview", listReview);
//        model.addAttribute("startAverage", startAverage);
//        model.addAttribute("totalReview", listReview.size());
//
//        return "tour_detail";
//    }

    // ==================== TOUR MANAGEMENT PAGE (ADMIN) ====================
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

    @GetMapping("/search")
    public String searchTours(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        model.addAttribute("tours", tourServices.searchTours(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalTours", tourServices.getTotalTours());
        model.addAttribute("activeTours", tourServices.getActiveTours());
        model.addAttribute("expectedRevenue", tourServices.getExpectedRevenue());
        model.addAttribute("admin", loggedInAdmin);
        return "admin_tour_management";
    }

    // ĐÃ SỬA LỖI TRÙNG URL: Chuyển đường dẫn của Admin thành /manager/detail/{id}
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

    @PostMapping("/save")
    public String saveTour(@RequestParam("title") String title, @RequestParam("description") String description,
                           @RequestParam("durationDays") Integer durationDays, @RequestParam("durationNights") Integer durationNights,
                           HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        Tour tour = new Tour();
        tour.setTitle(title); tour.setDescription(description);
        tour.setDurationDays(durationDays); tour.setDurationNights(durationNights); tour.setIsActive(true);
        if (tourServices.saveTour(tour) != null) return "redirect:/tour/manager?success=Thêm thành công";
        return "admin_tour_management";
    }

    @GetMapping("/delete/{id}")
    public String deleteTour(@PathVariable(value = "id") Integer tourId, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        if (tourServices.deactivateTour(tourId)) return "redirect:/tour/manager?success=Đã xóa";
        return "redirect:/tour/manager?error=Lỗi khi xóa tour";
    }

    //xem tour
    @GetMapping("/tours/by-province/{provinceId}")
    public String getToursByProvince(@PathVariable Integer provinceId, Model model) {
        List<Tour> tours = tourRepository.findByProvince_ProvinceId(provinceId);
        model.addAttribute("tours", tours);
        return "tours_by_province"; // view hiển thị danh sách tour
    }


}



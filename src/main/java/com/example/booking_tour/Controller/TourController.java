package com.example.booking_tour.Controller;


import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.*;
import com.example.booking_tour.Services.*;
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

    @Autowired
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
    @Autowired private ProvinceServices provinceServices;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private DepartureServices departureServices;

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
        model.addAttribute("listCategory", listTourCategory);
        model.addAttribute("listTours", tourRegion);
        return "list_tour";
    }

    // ==================== TOUR MANAGEMENT PAGE ====================
//
//    @GetMapping("/manager")
//    public String tourManagement(HttpSession session, Model model) {
//        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
//        if (loggedInAdmin == null) return "redirect:/admin/loginForm";
    // ==================== CỦA KHÁCH HÀNG: Xem chi tiết ====================

    // ==================== TOUR MANAGEMENT PAGE (ADMIN) ====================
    @GetMapping("/manager")
    public String tourManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";
        try {
            model.addAttribute("totalTours", tourServices.getTotalTours());
            model.addAttribute("activeTours", tourServices.getActiveTours());
            model.addAttribute("expectedRevenue", tourServices.getExpectedRevenue()); // Trả lại hàm gốc
            model.addAttribute("tours", tourServices.getAllTours());
            model.addAttribute("admin", loggedInAdmin);
            model.addAttribute("categories", TCServices.getAllTourCategories());
            model.addAttribute("provinces", provinceServices.getAllProvinces());
            return "admin_tour_management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            return "admin_tour_management";
        }
    }

    @GetMapping("/search")
    public String searchTours(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

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

    //  Chuyển đường dẫn của Admin thành /manager/detail/{id}
    @GetMapping("/manager/detail/{id}")
    public String getTourDetailAdmin(@PathVariable(value = "id") Integer tourId, HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        Tour tour = tourServices.getTourById(tourId);
        if (tour == null) {
            return "redirect:/tour/manager";
        }

        model.addAttribute("tour", tour);
        // Lấy thêm Lịch trình và Hình ảnh để hiển thị trong trang chi tiết
        model.addAttribute("listSchedule", TSCServices.getTourSchedulesByTour(tour));
        model.addAttribute("listImages", IServices.getAllImagesToursByTour(tour));
        model.addAttribute("admin", loggedInAdmin);

        return "admin_tour_detail"; // Trỏ sang file HTML mới
    }

    // ==========================================
    // THÊM TOUR MỚI
    // ==========================================
    // ==========================================
    // THÊM TOUR MỚI (CHUẨN LOGIC)
    // ==========================================
    @PostMapping("/save")
    public String saveTour(
            @RequestParam("title") String title,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("provinceId") Integer provinceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("durationDays") Integer durationDays,
            @RequestParam("durationNights") Integer durationNights,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        // Kiểm tra Tên Tour trống
        if (title == null || title.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Tên Tour không được để trống!");
            return "redirect:/tour/add";
        }

        // 1. KIỂM TRA LOGIC NGÀY/ĐÊM
        if (durationNights > durationDays) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Số đêm không được lớn hơn số ngày!");
            return "redirect:/tour/add";
        }
        if (durationDays - durationNights > 1) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Số ngày và số đêm không hợp lý (VD chuẩn: 3 ngày 2 đêm)!");
            return "redirect:/tour/add";
        }

        // 2. LƯU TOUR VÀO DATABASE
        try {
            Tour tour = new Tour();
            tour.setTitle(title);
            tour.setDescription(description);
            tour.setDurationDays(durationDays);
            tour.setDurationNights(durationNights);

            // Gán Danh mục
            TourCategory category = new TourCategory();
            category.setCategoryId(categoryId);
            tour.setCategory(category);

            // Gán Địa điểm
            Province province = new Province();
            province.setProvinceId(provinceId);
            tour.setProvince(province);

            tourServices.saveTour(tour);

            redirectAttributes.addFlashAttribute("message", "Thêm Tour mới thành công!");
            return "redirect:/tour/manager"; // Về lại danh sách quản lý

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống khi lưu: " + e.getMessage());
            return "redirect:/tour/add";
        }
    }
    // ==========================================
    // FORM SỬA TOUR
    // ==========================================
    @GetMapping("/edit/{id}")
    public String showEditTourForm(@PathVariable("id") Integer tourId, Model model, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        Tour tour = tourServices.getTourById(tourId);
        if (tour == null) return "redirect:/tour/manager";

        model.addAttribute("tour", tour);

        // Bơm dữ liệu cho 2 thẻ <select>
        model.addAttribute("categories", TCServices.getAllTourCategories());
        model.addAttribute("provinces", provinceServices.getAllProvinces()); // ĐÃ FIX LỖI NULL

        return "edit_tour";
    }
    // ==========================================
    // THÊM TOUR MỚI (TRANG RIÊNG) Admin
    // ==========================================
    @GetMapping("/add")
    public String showAddTourForm(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        // Bơm dữ liệu cho các thẻ <select>
        model.addAttribute("categories", TCServices.getAllTourCategories());
        model.addAttribute("provinces", provinceServices.getAllProvinces());

        return "add_tour";
    }
    // Sửa thành chỉ còn /delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteTour(@PathVariable(value = "id") Integer tourId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        if (tourServices.deactivateTour(tourId)) {
            redirectAttributes.addFlashAttribute("message", "Đã xóa tour thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Không thể xóa tour này!");
        }

        // Quay lại đúng địa chỉ trang quản lý
        return "redirect:/tour/manager";
    }
    // ==========================================
    // LƯU CẬP NHẬT TOUR
    // ==========================================
    @PostMapping("/update")
    public String updateTour(
            @RequestParam("tourId") Integer tourId,
            @RequestParam("title") String title,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("provinceId") Integer provinceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("durationDays") Integer durationDays,
            @RequestParam("durationNights") Integer durationNights,
            @RequestParam(value = "pickupPoint", required = false) String pickupPoint,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        // Kiểm tra Tên Tour trống
        if (title == null || title.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Tên Tour không được để trống!");
            return "redirect:/tour/edit/" + tourId;
        }

        // ==========================================
        // 1. KIỂM TRA LOGIC NGÀY/ĐÊM
        // ==========================================
        if (durationNights > durationDays) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Số đêm không được lớn hơn số ngày!");
            return "redirect:/tour/edit/" + tourId; // Đá ngược lại trang Sửa của đúng Tour này
        }
        if (durationDays - durationNights > 1) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Số ngày và số đêm không hợp lý (VD chuẩn: 3 ngày 2 đêm)!");
            return "redirect:/tour/edit/" + tourId;
        }

        // ==========================================
        // 2. LƯU CẬP NHẬT
        // ==========================================
        try {
            Tour existingTour = tourServices.getTourById(tourId);
            if (existingTour != null) {
                existingTour.setTitle(title);
                existingTour.setDescription(description);
                existingTour.setDurationDays(durationDays);
                existingTour.setDurationNights(durationNights);
                existingTour.setPickupPoint(pickupPoint);

                // Cập nhật Danh mục và Địa điểm
                TourCategory category = new TourCategory();
                category.setCategoryId(categoryId);
                existingTour.setCategory(category);

                Province province = new Province();
                province.setProvinceId(provinceId);
                existingTour.setProvince(province);

                tourServices.saveTour(existingTour);
                redirectAttributes.addFlashAttribute("message", "Cập nhật Tour thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy Tour cần sửa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật Tour: " + e.getMessage());
        }
        // Sửa xong thì cho về lại trang danh sách Tour
        return "redirect:/tour/manager";
    }

    //xem tour
    @GetMapping("/tours/by-province/{provinceId}")
    public String getToursByProvince(@PathVariable Integer provinceId, Model model) {
        List<Tour> tours = tourRepository.findByProvince_ProvinceId(provinceId);
        model.addAttribute("tours", tours);
        return "tours_by_province"; // view hiển thị danh sách tour
    }


    // ==========================================
    // UPLOAD ẢNH LÊN FIREBASE CHO TOUR
    // ==========================================
    @PostMapping("/upload-image")
    public String uploadTourImage(
            @RequestParam("tourId") Integer tourId,
            @RequestParam("imageFile") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "isThumbnail", required = false) Boolean isThumbnail,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/admin/loginForm";

        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file ảnh!");
                return "redirect:/tour/manager/detail/" + tourId;
            }

            // Gọi hàm đẩy ảnh lên Firebase và nhận về cái Link
            String imageUrl = firebaseService.uploadImage(file);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Lưu đường link URL vào Database
                Tour tour = tourServices.getTourById(tourId);
                boolean isThumb = (isThumbnail != null) ? isThumbnail : false;

                ImagesTour newImage = new ImagesTour(imageUrl, tour, isThumb);
                IServices.saveImageTour(newImage);

                redirectAttributes.addFlashAttribute("message", "Tải ảnh lên Firebase thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lỗi: Không lấy được link ảnh từ Firebase!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi upload: " + e.getMessage());
        }

        return "redirect:/tour/manager/detail/" + tourId;
    }
    // ==========================================
    // XÓA ẢNH CỦA TOUR
    // ==========================================
    @GetMapping("/image/delete/{id}")
    public String deleteImage(@PathVariable("id") Integer imgId, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            ImagesTour image = IServices.getImageById(imgId);
            if (image != null) {
                Integer tourId = image.getTour().getTourId();
                IServices.deleteImageTour(imgId);
                redirectAttributes.addFlashAttribute("message", "Đã xóa ảnh thành công!");
                return "redirect:/tour/manager/detail/" + tourId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa ảnh!");
        }
        return "redirect:/tour/manager";
    }

    @GetMapping("filter_tour")
    public String filter_tour(
            @RequestParam(value = "durationDay", required = false, defaultValue = "30") Integer durationDay,
            @RequestParam(value = "categoriesId", required = false) List<Integer> categoriesId,
            @RequestParam(value = "maxPrice", required = false, defaultValue = "100000000") Double maxPrice,
            Model model) {
        List<Tour> listTourFilter = TServices.filterTourByCategoryMaxPriceAndDurationDay(durationDay, categoriesId, maxPrice);
        model.addAttribute("listTours", listTourFilter);
        model.addAttribute("selectedCategories", categoriesId);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentDuration", durationDay);
        model.addAttribute("listCategory", TCServices.getAllTourCategories());
        return "list_tour";
    }
}

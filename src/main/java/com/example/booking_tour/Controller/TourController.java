package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.*;
import com.example.booking_tour.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tour")
public class TourController {

    @Autowired
    private TourServices tourServices;

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

    @Autowired private ProvinceServices provinceServices;

    @Autowired
    private FirebaseService firebaseService;


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

    // ==================== TOUR MANAGEMENT PAGE (ADMIN) ====================
    @GetMapping("/manager")
    public String tourManagement(HttpSession session, Model model) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

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
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        model.addAttribute("tours", tourServices.searchTours(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalTours", tourServices.getTotalTours());
        model.addAttribute("activeTours", tourServices.getActiveTours());
        model.addAttribute("expectedRevenue", tourServices.getExpectedRevenue());
        model.addAttribute("admin", loggedInAdmin);
        return "admin_tour_management";
    }

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
    @PostMapping("/save")
    public String saveTour(
            @RequestParam("title") String title,
            @RequestParam("categoryId") Integer categoryId, // Lấy ID danh mục
            @RequestParam("provinceId") Integer provinceId, // Lấy ID địa điểm
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("durationDays") Integer durationDays,
            @RequestParam("durationNights") Integer durationNights,
            HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        try {
            Tour tour = new Tour();
            tour.setTitle(title);
            tour.setDescription(description);
            tour.setDurationDays(durationDays);
            tour.setDurationNights(durationNights);
            tour.setIsActive(true);
            tour.setCreatedBy(loggedInAdmin); // Lưu người tạo

            // Tạo đối tượng giả để gán khóa ngoại (Tránh phải query DB)
            TourCategory category = new TourCategory();
            category.setCategoryId(categoryId);
            tour.setCategory(category);

            Province province = new Province();
            province.setProvinceId(provinceId);
            tour.setProvince(province);

            tourServices.saveTour(tour);
            redirectAttributes.addFlashAttribute("message", "Thêm Tour mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thêm Tour: " + e.getMessage());
        }

        return "redirect:/tour/manager";
    }

    // ==========================================
    // MỞ FORM SỬA TOUR
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

    @GetMapping("/delete/{id}")
    public String deleteTour(@PathVariable(value = "id") Integer tourId, HttpSession session) {
        Employee loggedInAdmin = (Employee) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/auth/loginForm";

        if (tourServices.deactivateTour(tourId)) return "redirect:/tour/manager?success=Đã xóa";
        return "redirect:/tour/manager?error=Lỗi khi xóa tour";
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

        return "redirect:/tour/manager";
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
}
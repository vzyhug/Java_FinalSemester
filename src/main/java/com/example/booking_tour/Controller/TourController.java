package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.*;
import com.example.booking_tour.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
public class TourController {

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

    @GetMapping("")
    @Transactional(readOnly = true)
    public String list_tour(Model model, @RequestParam(value = "sort", required = false) String sort) {
        List<Tour> listTour;
        List<TourCategory> listTourCategory = TCServices.getAllTourCategories();
        if ("asc".equals(sort)) {
            listTour = TServices.sortTourByPrice(true);
        }
        else if ("desc".equals(sort)) {
            listTour = TServices.sortTourByPrice(false);
        }
        else
        {
            listTour = TServices.getAllTours();
        }
        model.addAttribute("listCategory", listTourCategory);
        model.addAttribute("listTours", listTour);
        model.addAttribute("currentSort", sort);
        return "list_tour";
    }

    @GetMapping("region/{region}")
    @Transactional(readOnly = true)
    public String list_tour_region(Model model,@PathVariable(value = "region") String regionName) {
        List<Tour> listTour=TServices.getAllTours();
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
        Tour tourDetail=TServices.getToursByTourId(tourId);
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
}



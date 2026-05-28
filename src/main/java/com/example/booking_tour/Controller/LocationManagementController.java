package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Repository.ProvinceRepository;
import com.example.booking_tour.Repository.TourRepository;
import com.example.booking_tour.Services.LocationServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LocationManagementController {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private LocationServices locationService;

    @GetMapping("/location_management")
    public String locationManagement(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {

        Pageable pageable = PageRequest.of(page, 4);

        Page<Tour> tourPage;

        if (!keyword.isEmpty()) {

            tourPage = tourRepository
                    .findByTitleContainingIgnoreCase(
                            keyword,
                            pageable
                    );

        } else {

            tourPage = tourRepository.findAll(pageable);
        }

        model.addAttribute(
                "totalProvince",
                provinceRepository.count()
        );

        model.addAttribute(
                "totalTours",
                tourRepository.count()
        );

        model.addAttribute(
                "provinceList",
                locationService.getProvinceData()
        );

        // chỉ lấy 6 tỉnh
        model.addAttribute(
                "topProvinceList",
                locationService.getProvinceData()
                        .stream()
                        .limit(6)
                        .toList()
        );

        model.addAttribute(
                "recentTours",
                tourPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                tourPage.getTotalPages()
        );

        model.addAttribute(
                "keyword",
                keyword
        );
        return "location_management";
    }
}
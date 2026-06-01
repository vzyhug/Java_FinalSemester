package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    @Autowired
    private ProvinceRepository provinceRepository;

    // ================= REDIRECT ROOT =================
    // - Chuyển hướng người dùng về trang quản lý địa điểm tổng hợp để tránh lỗi 404
    // =================================================
    @GetMapping
    public String index() {
        return "redirect:/location_management";
    }

    // ================= FORM ADD =================
    @GetMapping("/add")
    public String addProvinceForm(Model model) {

        model.addAttribute("province", new Province());

        model.addAttribute(
                "regions",
                Province.Region.values()
        );

        // TITLE
        model.addAttribute(
                "parentTitle",
                "Quản lý Địa điểm"
        );

        model.addAttribute(
                "currentTitle",
                "Thêm địa điểm mới"
        );

        model.addAttribute(
                "pageDescription",
                "Thêm tỉnh thành và vùng miền"
        );

        return "add_province";
    }

    // ================= SAVE =================
    @PostMapping("/save")
    public String saveProvince(
            @ModelAttribute Province province,
            Model model
    ) {

        // CHECK RỖNG
        if (province.getProvinceName() == null
                || province.getProvinceName().trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Tên tỉnh thành không được để trống!"
            );

            model.addAttribute(
                    "regions",
                    Province.Region.values()
            );

            return "add_province";
        }

        // CHECK TRÙNG
        boolean exists =
                provinceRepository.existsByProvinceName(
                        province.getProvinceName().trim()
                );

        if (exists) {

            model.addAttribute(
                    "error",
                    "Tên tỉnh thành đã tồn tại!"
            );

            model.addAttribute(
                    "province",
                    province
            );

            model.addAttribute(
                    "regions",
                    Province.Region.values()
            );

            return "add_province";
        }

        // SAVE
        province.setProvinceName(
                province.getProvinceName().trim()
        );

        provinceRepository.save(province);

        // QUAY LẠI TRANG QUẢN LÝ
        return "redirect:/location_management";
    }
}
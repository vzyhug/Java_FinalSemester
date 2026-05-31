package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServices {

    @Autowired
    private ProvinceRepository provinceRepository;

    // Hàm lấy toàn bộ danh sách tỉnh thành để đổ vào thẻ <select>
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }
}
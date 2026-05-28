package com.example.booking_tour.Services;

import com.example.booking_tour.DTO.ProvinceDTOView;
import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Repository.ProvinceRepository;
import com.example.booking_tour.Repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServices {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private TourRepository tourRepository;

    public List<ProvinceDTOView> getProvinceData() {

        List<Province> provinces = provinceRepository.findAll();

        List<ProvinceDTOView> result = new ArrayList<>();

        for (Province province : provinces) {

            Integer totalTours =
                    tourRepository.countByProvince(province);

            ProvinceDTOView dto = new ProvinceDTOView(
                    province.getProvinceId(),
                    province.getProvinceName(),
                    province.getRegion().name(),
                    totalTours
            );

            result.add(dto);
        }

        return result;
    }

}

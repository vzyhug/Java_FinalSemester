package com.example.booking_tour.Services;

import com.example.booking_tour.Model.TourCategory;
import com.example.booking_tour.Repository.TourCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourCategoryServices {
    @Autowired
    TourCategoryRepository repo;

    //Lấy ra danh sách loại tour
    public List<TourCategory> getAllTourCategories(){
        return repo.findAll();
    }
}

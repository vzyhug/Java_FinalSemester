package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourSchedule;
import com.example.booking_tour.Repository.TourScheduleRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourScheduleServices {
    @Autowired
    private TourScheduleRepository repo;

    //Lấy danh sách schedule dựa trên id tour
    public List<TourSchedule> getTourSchedulesByTour(Tour tour){
        return repo.findByTour(tour);
    }
}

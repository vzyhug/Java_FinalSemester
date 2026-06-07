package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TourScheduleRepository extends JpaRepository<TourSchedule,Integer> {
    //================= tim kiem ==================
    //Tim kiem lich trinh tour theo tour id
    public List<TourSchedule> findByTour(Tour tour);

    //===================== sort danh sach ===================
}

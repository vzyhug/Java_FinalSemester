package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    List<Tour> findTop6ByOrderByTourIdDesc();
    Integer countByProvince(Province province);
    List<Tour> findByProvince(Province province);

    Page<Tour> findAll(Pageable pageable);
    Page<Tour> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}

package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourCategoryRepository extends JpaRepository<TourCategory,Integer> {
}

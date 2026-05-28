package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Review;
import com.example.booking_tour.Model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    public List<Review> findByTour(Tour tour);
}

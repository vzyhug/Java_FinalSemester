package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.ImagesTour;
import com.example.booking_tour.Model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesTourRepository extends JpaRepository<ImagesTour,Integer>
{
    //================= tim kiem ==================
    public List<ImagesTour> findByTour(Tour tour_id);
    public ImagesTour findTop1ByTourAndIsThumbnailTrue(Tour tour_id);

    //===================== sort danh sach ===================
}

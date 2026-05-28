package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Repository.TourDepartureRepository;
import com.example.booking_tour.Repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourServices { // Đã giữ nguyên tên có chữ 's'

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourDepartureRepository tourDepartureRepository;

    // ==================== CÁC HÀM CỦA THÙY (PUBLIC) ====================
    public List<Tour> sortTourByPrice(boolean isAcs) {
        if(isAcs) return tourRepository.findAllByOrderByMinPriceAsc();
        return tourRepository.findAllByOrderByMinPriceDesc();
    }

    public List<Tour> getTourByProvince(Province province) {
        return tourRepository.findByProvince(province);
    }

    public List<Tour> get4Tours() {
        return tourRepository.findTop4By();
    }

    // ==================== CÁC HÀM CỦA BẠN (ADMIN & THỐNG KÊ) ====================
    public Long getTotalTours() {
        try { return tourRepository.count(); } catch (Exception e) { return 0L; }
    }

    public Long getActiveTours() {
        try {
            List<Tour> activeTours = tourRepository.findByIsActive(true);
            return (long) (activeTours != null ? activeTours.size() : 0);
        } catch (Exception e) { return 0L; }
    }

    public java.math.BigDecimal getExpectedRevenue() {
        try {
            List<TourDeparture> departures = tourDepartureRepository.findAll();
            java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
            if (departures != null && !departures.isEmpty()) {
                for (TourDeparture departure : departures) {
                    int bookedSeats = departure.getMaxSeats() - departure.getAvailableSeats();
                    java.math.BigDecimal departureRevenue = departure.getAdultPrice()
                            .multiply(java.math.BigDecimal.valueOf(bookedSeats));
                    totalRevenue = totalRevenue.add(departureRevenue);
                }
            }
            return totalRevenue;
        } catch (Exception e) { return java.math.BigDecimal.ZERO; }
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour getTourById(Integer tourId) {
        return tourRepository.findById(tourId).orElse(null);
    }

    public List<Tour> searchTours(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) return getAllTours();
            return tourRepository.findByTitleContaining(keyword);
        } catch (Exception e) { return null; }
    }

    public List<Tour> getToursByCategory(Integer categoryId) {
        return tourRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Tour> getToursByProvince(Integer provinceId) {
        return tourRepository.findByProvince_ProvinceId(provinceId);
    }

    public List<Tour> getActiveToursList() {
        return tourRepository.findByIsActive(true);
    }

    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    public boolean deleteTour(Integer tourId) {
        try { tourRepository.deleteById(tourId); return true; } catch (Exception e) { return false; }
    }

    public boolean deactivateTour(Integer tourId) {
        try {
            Tour tour = getTourById(tourId);
            if (tour != null) {
                tour.setIsActive(false);
                tourRepository.save(tour);
                return true;
            }
            return false;
        } catch (Exception e) { return false; }
    }
}
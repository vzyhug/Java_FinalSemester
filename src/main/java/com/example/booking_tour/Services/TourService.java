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
public class TourService {
    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourDepartureRepository tourDepartureRepository;

    // ==================== STATISTICS ====================

    /**
     * Lấy tổng số tour trong hệ thống
     */
    public Long getTotalTours() {
        try {
            return tourRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalTours: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy số tour đang hoạt động (isActive = true)
     */
    public Long getActiveTours() {
        try {
            List<Tour> activeTours = tourRepository.findByIsActive(true);
            return (long) (activeTours != null ? activeTours.size() : 0);
        } catch (Exception e) {
            System.out.println("Lỗi trong getActiveTours: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Tính tổng doanh thu dự kiến từ tất cả tour
     */
    public java.math.BigDecimal getExpectedRevenue() {
        try {
            List<TourDeparture> departures = tourDepartureRepository.findAll();
            java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;

            if (departures != null && !departures.isEmpty()) {
                for (TourDeparture departure : departures) {
                    // Tính số khách đã đặt = maxSeats - availableSeats
                    int bookedSeats = departure.getMaxSeats() - departure.getAvailableSeats();

                    // Tính doanh thu của chuyến này = số khách * giá người lớn
                    java.math.BigDecimal departureRevenue = departure.getAdultPrice()
                            .multiply(java.math.BigDecimal.valueOf(bookedSeats));

                    totalRevenue = totalRevenue.add(departureRevenue);
                }
            }

            return totalRevenue;
        } catch (Exception e) {
            System.out.println("Lỗi trong getExpectedRevenue: " + e.getMessage());
            return java.math.BigDecimal.ZERO;
        }
    }

    // ==================== DATA RETRIEVAL ====================

    /**
     * Lấy danh sách TẤT CẢ tour
     */
    public List<Tour> getAllTours() {
        try {
            return tourRepository.findAll();
        } catch (Exception e) {
            System.out.println("Lỗi trong getAllTours: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy tour theo ID
     */
    public Tour getTourById(Integer tourId) {
        try {
            return tourRepository.findById(tourId).orElse(null);
        } catch (Exception e) {
            System.out.println("Lỗi trong getTourById: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tìm tour theo tên (fuzzy search)
     */
    public List<Tour> searchTours(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllTours();
            }
            return tourRepository.findByTitleContaining(keyword);
        } catch (Exception e) {
            System.out.println("Lỗi trong searchTours: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy tour theo danh mục
     */
    public List<Tour> getToursByCategory(Integer categoryId) {
        try {
            return tourRepository.findByCategory_CategoryId(categoryId);
        } catch (Exception e) {
            System.out.println("Lỗi trong getToursByCategory: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy tour theo tỉnh/thành phố
     */
    public List<Tour> getToursByProvince(Integer provinceId) {
        try {
            return tourRepository.findByProvince_ProvinceId(provinceId);
        } catch (Exception e) {
            System.out.println("Lỗi trong getToursByProvince: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các tour hoạt động (để hiển thị cho khách hàng)
     */
    public List<Tour> getActiveToursList() {
        try {
            return tourRepository.findByIsActive(true);
        } catch (Exception e) {
            System.out.println("Lỗi trong getActiveToursList: " + e.getMessage());
            return null;
        }
    }

    // ==================== CRUD OPERATIONS ====================

    /**
     * Thêm/Cập nhật tour
     */
    public Tour saveTour(Tour tour) {
        try {
            return tourRepository.save(tour);
        } catch (Exception e) {
            System.out.println("Lỗi trong saveTour: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xóa tour
     */
    public boolean deleteTour(Integer tourId) {
        try {
            tourRepository.deleteById(tourId);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi trong deleteTour: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deactivate tour (không xóa, chỉ ẩn đi)
     */
    public boolean deactivateTour(Integer tourId) {
        try {
            Tour tour = getTourById(tourId);
            if (tour != null) {
                tour.setIsActive(false);
                tourRepository.save(tour);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Lỗi trong deactivateTour: " + e.getMessage());
            return false;
        }
    }

    //------- Thuy - Tourservice -----------
    @Autowired
    TourRepository repo;

    //Lấy tour dựa trên id
    public Tour getToursByTourId(Integer id)
    {
        return repo.findById(id).orElse(null);
    }

    //Sort danh sách trên giá
    public List<Tour> sortTourByPrice(boolean isAcs)
    {
        if(isAcs)
        {
            return repo.findAllByOrderByMinPriceAsc();
        }
        return repo.findAllByOrderByMinPriceDesc();
    }

    //Lấy danh sách tour dựa trên provide
    public List<Tour> getTourByProvince(Province province)
    {
        return repo.findByProvince(province);
    }

    public List<Tour> get4Tours()
    {
        return repo.findTop4By();
    }
}

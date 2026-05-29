package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer> {

    // ==================== 1. TÌM KIẾM THEO TOUR & NHÂN VIÊN ====================

    /**
     * Tìm chuyến đi theo ID của Tour (Dùng khi chỉ có ID)
     */
    List<TourDeparture> findByTour_TourId(Integer tourId);

    /**
     * Tìm kiếm Tour departure thông qua object Tour (Dùng khi có sẵn object Tour)
     */
    List<TourDeparture> findByTour(Tour tour);

    /**
     * Tìm chuyến đi theo ID của Hướng dẫn viên
     */
    List<TourDeparture> findByGuide_EmployeeId(Integer employeeId);


    // ==================== 2. TÌM KIẾM THEO NGÀY THÁNG ====================

    /**
     * Tìm chuyến đi theo ngày khởi hành
     */
    List<TourDeparture> findByDepartureDate(LocalDate departureDate);

    /**
     * Tìm kiếm tour departure bằng ngày về
     */
    List<TourDeparture> findByReturnDate(LocalDate returnDate);

    /**
     * Tìm kiếm tour dựa trên khoảng ngày đi và về
     */
    List<TourDeparture> findByDepartureDateAndReturnDate(LocalDate departureDate, LocalDate returnDate);


    // ==================== 3. TÌM KIẾM THEO TRẠNG THÁI & CHỖ NGỒI ====================

    /**
     * Tìm chuyến đi theo trạng thái (ví dụ: open, full, cancelled)
     */
    List<TourDeparture> findByStatus(String status);

    /**
     * Tìm kiếm tour departure bằng số ghế trống
     */
    List<TourDeparture> findByAvailableSeats(int availableSeats);


    // ==================== 4. SẮP XẾP DANH SÁCH ====================

    /**
     * Sắp xếp theo giá vé người lớn (Tăng dần - Thấp đến cao)
     */
    List<TourDeparture> findAllByOrderByAdultPriceAsc();

    /**
     * Sắp xếp theo giá vé người lớn (Giảm dần - Cao xuống thấp)
     */
    List<TourDeparture> findAllByOrderByAdultPriceDesc();
}
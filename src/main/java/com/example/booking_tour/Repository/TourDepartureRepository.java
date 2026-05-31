package com.example.booking_tour.Repository;


import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.booking_tour.Model.Tour;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer> {




    // ==================== 1. TÌM KIẾM THEO TOUR & NHÂN VIÊN ====================

    List<TourDeparture> findByTour_TourId(Integer tourId);

    List<TourDeparture> findByTour(Tour tour);

    List<TourDeparture> findByGuide_EmployeeId(Integer employeeId);

    // ==================== 2. TÌM KIẾM THEO NGÀY THÁNG ====================

    List<TourDeparture> findByDepartureDate(LocalDate departureDate);

    List<TourDeparture> findByReturnDate(LocalDate returnDate);

    List<TourDeparture> findByDepartureDateAndReturnDate(LocalDate departureDate, LocalDate returnDate);

    // ==================== 3. TÌM KIẾM THEO TRẠNG THÁI & CHỖ NGỒI ====================

    List<TourDeparture> findByStatus(String status);

    List<TourDeparture> findByAvailableSeats(int availableSeats);

    // ==================== 4. SẮP XẾP DANH SÁCH ====================

    List<TourDeparture> findAllByOrderByAdultPriceAsc();

    List<TourDeparture> findAllByOrderByAdultPriceDesc();
}

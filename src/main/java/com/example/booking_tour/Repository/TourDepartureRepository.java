package com.example.booking_tour.Repository;


import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer> {

    // ==================== CUSTOM QUERIES ====================

    /**
     * ĐÃ SỬA: Thêm dấu gạch dưới (_) để map chính xác vào thuộc tính tourId
     * nằm bên trong thực thể Tour. Giúp DepartureService hết báo lỗi đỏ.
     */
    List<TourDeparture> findByTour_TourId(Integer tourId);

    /**
     * Tìm chuyến đi theo trạng thái (open, full, cancelled)
     */
    List<TourDeparture> findByStatus(String status);

    /**
     * Tìm chuyến đi theo ngày khởi hành
     */
    List<TourDeparture> findByDepartureDate(LocalDate departureDate);

    /**
     * ĐÃ SỬA: Thêm dấu gạch dưới (_) để map chính xác thuộc tính employeeId
     * của thực thể Employee (được đặt tên biến liên kết là guide).
     */
    List<TourDeparture> findByGuide_EmployeeId(Integer employeeId);

    //================= tim kiem ==================

    //Tim kiem Tour departure thong qua id tour
    public List<TourDeparture> findByTour(Tour tour);
    //Tim kiem tour departure bang ngay ve
    public List<TourDeparture> findByReturnDate(LocalDate returnDate);
    //Tim kiem tour dua tren ngay di va ve
    public List<TourDeparture> findByDepartureDateAndReturnDate(LocalDate departureDate,LocalDate returnDate);
    //Tim kiem tour departure bang so ghe trong
    public List<TourDeparture> findByAvailableSeats(int availableSeats);

    //===================== sort danh sach ===================
    //Sap xep theo gia ve
    //---- sort asc
    public List<TourDeparture> findAllByOrderByAdultPriceAsc();
    //---- sort desc
    public List<TourDeparture> findAllByOrderByAdultPriceDesc();
}


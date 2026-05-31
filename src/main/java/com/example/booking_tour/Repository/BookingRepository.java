package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Đã bổ sung thư viện này
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /**
     * Lấy 5 booking mới nhất theo ngày đặt
     */
    List<Booking> findTop5ByOrderByBookingDateDesc();

    /**
     * Lấy booking theo trạng thái
<<<<<<< HEAD
=======
     *
>>>>>>> origin/minhthu
     * @param status pending, confirmed, cancelled, completed
     */
    List<Booking> findByStatus(String status);

    /**
     * Lấy booking của khách hàng
     */
    List<Booking> findByCustomer_CustomerId(Integer customerId);

    /**
     * Lấy booking của chuyến đi
     */
    List<Booking> findByDeparture_DepartureId(Integer departureId);

    public List<Booking> findByCustomer(Customer customer);

    // ==========================================
    // TÍNH TỔNG DOANH THU (ALL-TIME)
    // ==========================================
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'completed' OR b.status = 'confirmed'")
    BigDecimal calculateTotalRevenue();

    // ==========================================
    // LỌC THEO KHOẢNG THỜI GIAN (DÙNG CHO DASHBOARD)
    // ==========================================
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE (b.status = 'completed' OR b.status = 'confirmed') AND b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    BigDecimal calculateTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    long countBookingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    // Thay đổi tham số từ LocalDate thành LocalDateTime
    @Query("SELECT b FROM Booking b WHERE " +
            "(:startDate IS NULL OR b.bookingDate >= :startDate) AND " +
            "(:endDate IS NULL OR b.bookingDate <= :endDate) " +
            "ORDER BY b.bookingDate DESC")
    List<Booking> findBookingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    long countByStatus(String status);
}

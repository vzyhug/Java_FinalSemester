package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> {
    /**
     * Lấy 5 booking mới nhất theo ngày đặt
     */
    List<Booking> findTop5ByOrderByBookingDateDesc();

    /**
     * Lấy booking theo trạng thái
     *
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
}

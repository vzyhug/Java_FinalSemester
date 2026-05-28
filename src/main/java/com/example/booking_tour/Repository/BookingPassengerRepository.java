package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingPassengerRepository extends JpaRepository<Booking, Integer> {
}

package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>
{
}

package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TourDepartureRepository extends JpaRepository<TourDeparture,Integer>
{
    //================= tim kiem ==================

    //Tim kiem Tour departure thong qua id tour
    public List<TourDeparture> findByTour(Tour tour);
    //Tim kiem tour departure bang ngay di
    public List<TourDeparture> findByDepartureDate(LocalDate departureDate);
    //Tim kiem tour departure bang ngay ve
    public List<TourDeparture> findByReturnDate(LocalDate returnDate);
    //Tim kiem tour dua tren ngay di va ve
    public List<TourDeparture> findByDepartureDateAndReturnDate(LocalDate departureDate,LocalDate returnDate);
    //Tim kiem tour departure bang so ghe trong
    public List<TourDeparture> findByAvailableSeats(int availableSeats);
    //Tim kiem tour theo trang thai
    public List<TourDeparture> findByStatus(String status);

    //===================== sort danh sach ===================
    //Sap xep theo gia ve
    //---- sort asc
    public List<TourDeparture> findAllByOrderByAdultPriceAsc();
    //---- sort desc
    public List<TourDeparture> findAllByOrderByAdultPriceDesc();
}

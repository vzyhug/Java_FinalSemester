package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tour_departures")
public class TourDeparture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departure_id")
    private Integer departureId;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "max_seats", nullable = false)
    private Integer maxSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "adult_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal adultPrice;

    @Column(name = "child_price", precision = 19, scale = 4)
    private BigDecimal childPrice;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Employee guide;

    @Column(name = "status", length = 20)
    private String status = "open"; // open, full, cancelled
}
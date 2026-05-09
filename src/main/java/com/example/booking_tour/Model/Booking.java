package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "booking_code", nullable = false, unique = true, length = 20)
    private String bookingCode;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "departure_id", nullable = false)
    private TourDeparture departure;

    @Column(name = "booking_date",columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(name = "total_adults", nullable = false)
    private Integer totalAdults = 1;

    @Column(name = "total_children")
    private Integer totalChildren = 0;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'pending'")
    private String status = "pending"; // pending, confirmed, cancelled, completed

    @ManyToOne
    @JoinColumn(name = "created_by_employee")
    private Employee createdByEmployee;
}
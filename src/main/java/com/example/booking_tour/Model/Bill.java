package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Integer billId;

    @Column(name = "bill_number", unique = true, length = 50)
    private String billNumber;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "bill_date",columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime billDate = LocalDateTime.now();

    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal finalAmount;
}
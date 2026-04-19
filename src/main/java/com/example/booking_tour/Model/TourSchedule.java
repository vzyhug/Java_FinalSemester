package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tour_schedules")
public class TourSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "activities")
    private String activities;

    @Column(name = "has_breakfast")
    private Boolean hasBreakfast = true;

    @Column(name = "has_lunch")
    private Boolean hasLunch = true;

    @Column(name = "has_dinner")
    private Boolean hasDinner = true;
}
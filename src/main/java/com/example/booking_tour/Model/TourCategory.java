package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tour_categories")
public class TourCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, unique = true, length = 50)
    private String categoryName;

    @Column(name = "description")
    private String description;
}
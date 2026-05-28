package com.example.booking_tour.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "review")
public class Review
{
    @Column(name = "review_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "tour_id",nullable = false)
    private Tour tour;

    @Column(name = "feedback",nullable = false)
    private String feedback;

    @Column(name = "rating",nullable = false)
    @Max(5)
    @Min(0)
    private double rating;

    public Review(Customer customer, Tour tour, String feedback, double rating)
    {
        this.customer=customer;
        this.tour=tour;
        this.feedback=feedback;
        this.rating=rating;
    }
}

package com.example.booking_tour.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Integer tourId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private TourCategory category;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "duration_nights", nullable = false)
    private Integer durationNights;

    @Column(name = "pickup_point")
    private String pickupPoint;

    @Column(name = "description")
    private String description;

    @Column(name = "itinerary_summary")
    private String itinerarySummary;

    @Column(name = "included_services")
    private String includedServices;

    @Column(name = "is_active",columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @Column(name = "min_price")
    private double minPrice;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @OneToMany(mappedBy = "tour", fetch = FetchType.EAGER)
    private Set<ImagesTour> images;

    public ImagesTour getImagesThumbnails() {
        for(ImagesTour image : images) {
            if(image.isThumbnail()) {
                return image;
            }
        }
        return null;
    }
}
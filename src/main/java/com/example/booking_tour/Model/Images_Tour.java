package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "images_tour")
@Entity
@NoArgsConstructor
public class Images_Tour
{
    @Column(name = "img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer imgId;

    @Column(name = "img_url", nullable = false)
    private String imgURL;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @Column(name = "is_thumbnail",columnDefinition = "BOOLEAN DEFAULT false")
    boolean isThumbnail;

    public Images_Tour(String imgURL,Tour tour, boolean isThumbnail)
    {
        this.imgURL = imgURL;
        this.tour = tour;
        this.isThumbnail = isThumbnail;
    }

    public Images_Tour(String imgURL,Tour tour)
    {
        this.imgURL = imgURL;
        this.tour = tour;
        this.isThumbnail = false;
    }
}

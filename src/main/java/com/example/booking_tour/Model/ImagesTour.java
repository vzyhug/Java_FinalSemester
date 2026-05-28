package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "images_tour")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImagesTour
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
    private boolean isThumbnail;

    public ImagesTour(String imgURL, Tour tour, boolean isThumbnail)
    {
        this.imgURL = imgURL;
        this.tour = tour;
        this.isThumbnail = isThumbnail;
    }

    public ImagesTour(String imgURL, Tour tour)
    {
        this.imgURL = imgURL;
        this.tour = tour;
        this.isThumbnail = false;
    }
}

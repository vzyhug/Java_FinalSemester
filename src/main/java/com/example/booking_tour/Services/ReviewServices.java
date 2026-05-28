package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Review;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServices
{
    @Autowired
    private ReviewRepository repo;

    public List<Review> getAllReviewByTour(Tour tour)
    {
        return repo.findByTour(tour);
    }

    //Lay ra so sao trung binh
    public double getStarAverage(List<Review> reviews)
    {
        double sum = 0;
        for(Review review : reviews)
        {
            sum+=review.getRating();
        }
        return sum/reviews.size();
    }

}

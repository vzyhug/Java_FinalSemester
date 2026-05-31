package com.example.booking_tour.Services;

import com.example.booking_tour.Model.ImagesTour;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Repository.ImagesTourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagesTourServices
{
    @Autowired
    ImagesTourRepository repo;

    public List<ImagesTour> getAllTours()
    {
        return repo.findAll();
    }

    //Lấy ra danh sách hình ảnh của tour
    public List<ImagesTour> getAllImagesToursByTour(Tour tour)
    {
        return repo.findByTour(tour);
    }

    //Lấy ra duy nhất ảnh thumbnail
    public ImagesTour getThumbnailImageByTour(Tour tour)
    {
        return repo.findTop1ByTourAndIsThumbnailTrue(tour);
    }


    public ImagesTour saveImageTour(ImagesTour imagesTour) {
        return repo.save(imagesTour);
    }

    public ImagesTour getImageById(Integer imgId) {
        return repo.findById(imgId).orElse(null);
    }

    public void deleteImageTour(Integer imgId) {
        repo.deleteById(imgId);
    }
}

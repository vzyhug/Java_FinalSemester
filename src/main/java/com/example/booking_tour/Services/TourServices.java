package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.Region;
import java.util.List;

@Service
public class TourServices
{
    @Autowired
    TourRepository repo;
    //Lấy danh sách tour
    public List<Tour> getAllTours()
    {
        return repo.findAll();
    }

    //Lấy tour dựa trên id
    public Tour getToursByTourId(Integer id)
    {
        return repo.findById(id).orElse(null);
    }

    //Sort danh sách trên giá
    public List<Tour> sortTourByPrice(boolean isAcs)
    {
        if(isAcs)
        {
            return repo.findAllByOrderByMinPriceAsc();
        }
        return repo.findAllByOrderByMinPriceDesc();
    }

    //Lấy danh sách tour dựa trên provide
    public List<Tour> getTourByProvince(Province province)
    {
        return repo.findByProvince(province);
    }

    public List<Tour> get4Tours()
    {
        return repo.findTop4By();
    }

}

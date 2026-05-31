package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Repository.TourDepartureRepository;
import com.example.booking_tour.Repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TourDepartureServices
{
    @Autowired
    TourDepartureRepository repoTD;
    TourRepository repoT;
    //Sap xep danh sach theo gia tour
    public List<TourDeparture> filterTourPrice(boolean isAsc)
    {
        if(isAsc==true)
            return repoTD.findAllByOrderByAdultPriceAsc();
        return repoTD.findAllByOrderByAdultPriceDesc();
    }

    //Tim kiem cac thong tin tour departure theo ngay
    public List<TourDeparture> findTour(LocalDate deparDate,LocalDate returnDate)
    {
        if(deparDate!=null && returnDate==null)
            return repoTD.findByDepartureDate(deparDate);
        else if(deparDate==null && returnDate!=null)
            return repoTD.findByReturnDate(returnDate);
        else if(deparDate==null && returnDate==null)
            return repoTD.findAll();
        else
            return repoTD.findByDepartureDateAndReturnDate(deparDate,returnDate);
    }

    //Lấy ra toàn bộ danh sách tour departure
    public List<TourDeparture> getAllTourDeparture()
    {
        return repoTD.findAll();
    }

    //Lấy ra danh sách tour departure theo thông tin tour
    public List<TourDeparture> getTourDepartureByTour(Tour tour)
    {
        return repoTD.findByTour(tour);
    }

    public TourDeparture getTourDepartureById(Integer id) {
        return repoTD.findById(id).orElse(null);
    }
    public List<TourDeparture> getTourDepartureByTourId(Integer tourId)
    {
        Tour t=repoT.findById(tourId).orElse(null);
        if(t!=null)
        {
            return repoTD.findByTour(t);
        }
        return null;
    }

}

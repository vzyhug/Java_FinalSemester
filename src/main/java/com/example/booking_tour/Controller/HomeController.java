package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourCategory;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.BookingServices;
import com.example.booking_tour.Services.TourCategoryServices;
import com.example.booking_tour.Services.TourDepartureServices;
import com.example.booking_tour.Services.TourServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller // -->return HTML
@RequestMapping("/")
public class HomeController {
    @Autowired
    TourCategoryServices TCServices;

    @Autowired
    TourDepartureServices TDService;

    @Autowired
    TourServices TService;

    @Autowired
    BookingServices BService;

    @GetMapping("")
    public String home(Model model)
    {
        List<Tour> listTour=TService.get4Tours();
        model.addAttribute("listTours",listTour);
        return "home";
    }



    @GetMapping("/filter_tour")
    public String filter_tour(Model model,
                              @RequestParam(value = "destination",required = false) String destination,
                              @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd/mm/yyyy")LocalDate startDate,
                              @RequestParam(value = "pickupPoint",required = false) String pickupPoint)
    {
        List<TourCategory> listCategory=TCServices.getAllTourCategories();
        List<TourDeparture> listTourD=TDService.getAllTourDeparture();
        List<Tour> listTourAfter=new ArrayList<Tour>();

        if(destination!="" && startDate!=null && pickupPoint!="")
        {
            for(TourDeparture tourD:listTourD)
            {
                if(tourD.getTour().getTitle().toLowerCase().contains(destination.toLowerCase()) &&
                        tourD.getDepartureDate().equals(startDate) &&
                        tourD.getTour().getPickupPoint().toLowerCase().contains(pickupPoint.toLowerCase()))
                {
                    {
                        Tour tourAfter=tourD.getTour();
                        if(!listTourAfter.contains(tourAfter))
                        {
                            listTourAfter.add(tourAfter);
                        }
                    }
                }
            }
        }
        else if(destination!="" && startDate!=null)
        {
            for(TourDeparture tourD:listTourD)
            {
                if(tourD.getTour().getTitle().toLowerCase().contains(destination.toLowerCase()) &&
                        tourD.getDepartureDate().equals(startDate))
                {
                    {
                        Tour tourAfter=tourD.getTour();
                        if(!listTourAfter.contains(tourAfter))
                        {
                            listTourAfter.add(tourAfter);
                        }
                    }
                }
            }
        }
        else if(destination!="" && pickupPoint!="")
        {
            for(TourDeparture tourD:listTourD)
            {
                if(tourD.getTour().getTitle().toLowerCase().contains(destination.toLowerCase()) &&
                        tourD.getTour().getPickupPoint().toLowerCase().contains(pickupPoint.toLowerCase()))
                {
                    {
                        Tour tourAfter=tourD.getTour();
                        if(!listTourAfter.contains(tourAfter))
                        {
                            listTourAfter.add(tourAfter);
                        }
                    }
                }
            }
        }
        else if(pickupPoint!="" && startDate!=null)
        {
            for(TourDeparture tourD:listTourD)
            {
                if(tourD.getDepartureDate().equals(startDate) &&
                        tourD.getTour().getPickupPoint().toLowerCase().contains(pickupPoint.toLowerCase()))
                {
                    {
                        Tour tourAfter=tourD.getTour();
                        if(!listTourAfter.contains(tourAfter))
                        {
                            listTourAfter.add(tourAfter);
                        }
                    }
                }
            }
        }
        else
        {
            //Tìm danh sách tour theo điểm đến
            if(destination!="")
            {
                for(TourDeparture tourD:listTourD)
                {
                    if(tourD.getTour().getTitle().toLowerCase().contains(destination.toLowerCase()))
                    {
                        Tour tourAfter=tourD.getTour();
                        if(!listTourAfter.contains(tourAfter))
                        {
                            listTourAfter.add(tourAfter);
                        }
                    }
                }
            }

            //Tìm ngày bắt đầu
            else if(startDate!=null)
            {
                for(TourDeparture tourD:listTourD)
                {
                    if(tourD.getDepartureDate().equals(startDate))
                    {
                        {
                            Tour tourAfter=tourD.getTour();
                            if(!listTourAfter.contains(tourAfter))
                            {
                                listTourAfter.add(tourAfter);
                            }
                        }
                    }
                }
            }
            //Tìm điểm khởi hành
            else if(pickupPoint!="")
            {
                for(TourDeparture tourD:listTourD)
                {
                    if(tourD.getTour().getPickupPoint().toLowerCase().contains(pickupPoint.toLowerCase()))
                    {
                        {
                            Tour tourAfter=tourD.getTour();
                            if(!listTourAfter.contains(tourAfter))
                            {
                                listTourAfter.add(tourAfter);
                            }
                        }
                    }
                }
            }
        }

        model.addAttribute("listTours",listTourAfter);
        model.addAttribute("listCategory",listCategory);

        return "list_tour";
    }



}

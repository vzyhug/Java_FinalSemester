package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/location")
public class LocationController {

    @GetMapping("")
    public String home()
    {
        return "location_management";
    }

}
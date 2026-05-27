package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocationController {

    @GetMapping("/")
    public String home(){
        return "location_management";
    }

    @GetMapping("/location_management")
    public String location_management(){
        return "location_management";
    }

}

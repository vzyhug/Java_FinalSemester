package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller // -->return HTML
@RestController //--> return JSON/Text
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/hello")
    public String HomePage() {
        return "home";
    }
}

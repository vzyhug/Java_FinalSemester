package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tour")
public class TourController {
    @GetMapping("")
    public String tour(Model model) {
        model.addAttribute("message", "Welcome to the Tour Page!");
        return "list_tour"; // Trả về tên của view (list_tour.html)
    }
}

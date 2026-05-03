package com.example.booking_tour.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // -->return HTML
@RequestMapping("/home")
public class HomeController {
    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to the Home Page!");
        return "home"; // Trả về tên của view (home.html)
    }
}

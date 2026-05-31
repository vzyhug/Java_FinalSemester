package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.BookingPassenger;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Tour;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private TourDepartureServices TDServices;

    @Autowired
    private BookingServices BServices;

    @Autowired
    private CustomerServices CServices;

    //Chuyen qua man hinh dat tour
    @GetMapping("")
    public String booking(Model model,
                          @RequestParam(value = "departureId") Integer departure_id,
                          @RequestParam(value = "adult", defaultValue = "1") BigDecimal adult,
                          @RequestParam(value = "child", defaultValue = "0") BigDecimal child,
                          HttpSession session) {

        TourDeparture departure = TDServices.getTourDepartureById(departure_id);
        Tour tour = departure.getTour();

        BigDecimal adultTotal = departure.getAdultPrice().multiply(adult);
        BigDecimal childTotal = departure.getChildPrice().multiply(child);
        BigDecimal totalAll = adultTotal.add(childTotal);
        Customer currentCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (currentCustomer == null) {
            return "login_form";
        }

        String fullName = currentCustomer.getFullName();
        String email = currentCustomer.getEmail();
        String phone = currentCustomer.getPhone();

        model.addAttribute("departure", departure);
        model.addAttribute("tour", tour);
        model.addAttribute("userFullName", fullName);
        model.addAttribute("userEmail", email);
        model.addAttribute("userPhone", phone);
        model.addAttribute("adultCount", adult);
        model.addAttribute("childCount", child);
        model.addAttribute("adultTotal", adultTotal);
        model.addAttribute("childTotal", childTotal);
        model.addAttribute("totalAll", totalAll);

        return "booking_tour_form";
    }

    //Lưu tạm thông tin ở trang 1
    @PostMapping("/save_stage1")
    public String saveStage1(@RequestParam("departureId") Integer departureId,
                             @RequestParam("adultCount") Integer adultCount,
                             @RequestParam("childCount") Integer childCount,
                             @RequestParam("adultTotal") BigDecimal adultTotal,
                             @RequestParam("childTotal") BigDecimal childTotal,
                             @RequestParam("totalAll") BigDecimal totalAll,
                             @RequestParam("userFullName") String userFullName,
                             @RequestParam("userPhone") String userPhone,
                             @RequestParam("userEmail") String userEmail,
                             HttpSession session) {

        Map<String, Object> bookingStage1 = new HashMap<>();
        bookingStage1.put("departureId", departureId);
        bookingStage1.put("adultCount", adultCount);
        bookingStage1.put("childCount", childCount);
        bookingStage1.put("adultTotal", adultTotal);
        bookingStage1.put("childTotal", childTotal);
        bookingStage1.put("totalAll", totalAll);
        bookingStage1.put("userFullName", userFullName);
        bookingStage1.put("userPhone", userPhone);
        bookingStage1.put("userEmail", userEmail);
        session.setAttribute("bookingStage1", bookingStage1);
        return "redirect:/booking/add_passenger";
    }

    @GetMapping("/add_passenger")
    public String add_passenger(Model model, HttpSession session) {
        Map<String, Object> bookingStage1 = (Map<String, Object>) session.getAttribute("bookingStage1");

        if (bookingStage1 == null) {
            return "redirect:/";
        }
        Integer departureId = (Integer) bookingStage1.get("departureId");
        TourDeparture departure = TDServices.getTourDepartureById(departureId);
        Tour tour = departure.getTour();

        model.addAttribute("departure", departure);
        model.addAttribute("tour", tour);
        model.addAttribute("adultCount", bookingStage1.get("adultCount"));
        model.addAttribute("childCount", bookingStage1.get("childCount"));
        model.addAttribute("adultTotal", bookingStage1.get("adultTotal"));
        model.addAttribute("childTotal", bookingStage1.get("childTotal"));
        model.addAttribute("totalAll", bookingStage1.get("totalAll"));

        return "passenger_form";
    }

    @PostMapping("/save_state2")
    public String saveState2(@RequestParam("passengerNames") List<String> names,
                             @RequestParam("passengerBirthdays") List<String> birthdays,
                             @RequestParam("passengerIds") List<String> idCards,
                             @RequestParam("passengerTypes") List<String> types,
                             HttpSession session) {

        List<BookingPassenger> passengerList = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            BookingPassenger passenger = new BookingPassenger();
            passenger.setFullName(names.get(i));
            passenger.setIdCard(idCards.get(i).trim().isEmpty() ? null : idCards.get(i).trim());
            if (birthdays.get(i) != null && !birthdays.get(i).isEmpty()) {
                passenger.setDateOfBirth(LocalDate.parse(birthdays.get(i)));
            }
            passenger.setPassengerType(BookingPassenger.PassengerType.valueOf(types.get(i).toLowerCase()));
            passengerList.add(passenger);
        }
        session.setAttribute("bookingPassengers", passengerList);
        return "payments_invoices";
    }
}

package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServices {
    @Autowired
    BookingRepository repo;
    //them booking vao danh sach

    //tim danh sach booking bang id
    public Booking getBookingById(int bookingId){
        return repo.findById(bookingId).orElse(null);
    }

    //Huy booking (cho phep huy hoan toan voi state=pending)
    public int cancelBooking(Integer bookingId)
    {
        Booking booking=repo.findById(bookingId).orElse(null);
        if(booking!=null && booking.getStatus().equals("Pending"))
        {
            booking.setStatus("cancelled");
            repo.save(booking);
            return 1;
        }
        return 0;
    }
}

package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.BookingPassenger;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Repository.BookingPassengerRepository;
import com.example.booking_tour.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingServices {
    @Autowired
    BookingRepository repo;

    @Autowired
    BookingPassengerRepository repoPassenger;
    //them booking vao danh sach
    public void addBooking(Booking booking){
        repo.save(booking);
    }

    //them danh sach hanh khach
    public void addPassenger(List<BookingPassenger> listPassenger){
        for (BookingPassenger p : listPassenger) {
            repoPassenger.save(p);
        }
    }


    //tim danh sach booking bang id
    public Booking getBookingById(int bookingId){
        return repo.findById(bookingId).orElse(null);
    }

    //tim danh sach hanh khach bang booking
    public List<BookingPassenger> getPassengerByBookingId(int bookingId)
    {
        Booking b=repo.findById(bookingId).orElse(null);
        if(b==null)
        {
            return null;
        }
        return repoPassenger.findByBooking(b);
    }

    //tim danh sach booking thong qua kh
    public List<Booking> getBookingByPassenger(Customer customer)
    {
        return repo.findByCustomer(customer);
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

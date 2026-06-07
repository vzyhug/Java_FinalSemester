package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.BookingPassenger;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Repository.BookingPassengerRepository;
import com.example.booking_tour.Repository.BookingRepository;
import com.example.booking_tour.Model.BookingPaymentDTO;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class BookingServices {
    @Autowired
    BookingRepository repo;

    @Autowired
    BookingPassengerRepository repoPassenger;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    TourDepartureServices tdServices;

    @Autowired
    private com.example.booking_tour.Repository.TourDepartureRepository tourDepartureRepository;

    // CẬP NHẬT SỐ CHỖ
    public void updateAvailableSeats(Integer departureId) {
        if (departureId == null) return;
        TourDeparture departure = tdServices.getTourDepartureById(departureId);
        if (departure != null) {
            List<Booking> bookings = repo.findByDeparture_DepartureId(departureId);
            int bookedSeats = 0;
            if (bookings != null) {
                for (Booking b : bookings) {
                    if (!"cancelled".equalsIgnoreCase(b.getStatus())) {
                        int adults = b.getTotalAdults() != null ? b.getTotalAdults() : 0;
                        int children = b.getTotalChildren() != null ? b.getTotalChildren() : 0;
                        bookedSeats += (adults + children);
                    }
                }
            }
            int available = departure.getMaxSeats() - bookedSeats;
            if (available < 0) {
                available = 0;
            }
            departure.setAvailableSeats(available);

            // Cập nhật trạng thái thông minh cho Chuyến khởi hành (chỉ thay đổi nếu chưa hủy/hoàn thành)
            if (!"cancelled".equalsIgnoreCase(departure.getStatus()) && !"completed".equalsIgnoreCase(departure.getStatus())) {
                if (available <= 0) {
                    departure.setStatus("full");
                } else if ("full".equals(departure.getStatus())) {
                    departure.setStatus("open");
                }
            }

            tourDepartureRepository.save(departure);
        }
    }

    //them booking vao danh sach
    public void addBooking(Booking booking){
        repo.save(booking);
        if (booking.getDeparture() != null) {
            updateAvailableSeats(booking.getDeparture().getDepartureId());
        }
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
        if(booking!=null && "Pending".equalsIgnoreCase(booking.getStatus()))
        {
            booking.setStatus("cancelled");
            repo.save(booking);
            if (booking.getDeparture() != null) {
                updateAvailableSeats(booking.getDeparture().getDepartureId());
            }
            return 1;
        }
        return 0;
    }

    @Transactional
    public void saveBookingAndPassengerAndPayment(BookingPaymentDTO dto, Map<String, Object> stage1, List<BookingPassenger> passengers, Customer customer) {
        // 1. Create Booking
        Booking booking = new Booking();
        String bookingCode = "BKG" + System.currentTimeMillis();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);

        Integer departureId = (Integer) stage1.get("departureId");
        TourDeparture departure = tdServices.getTourDepartureById(departureId);
        booking.setDeparture(departure);
        booking.setTotalAdults((Integer) stage1.get("adultCount"));
        booking.setTotalChildren((Integer) stage1.get("childCount"));
        booking.setTotalAmount((BigDecimal) stage1.get("totalAll"));
        booking.setStatus("confirmed");
        repo.save(booking);

        // Cập nhật số chỗ sau khi booking được tạo thành công
        if (departure != null) {
            updateAvailableSeats(departure.getDepartureId());
        }

        // 2. Save Passengers
        for (BookingPassenger p : passengers) {
            p.setBooking(booking);
            repoPassenger.save(p);
        }

        // 3. Save Payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount((BigDecimal) stage1.get("totalAll"));
        payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "Chưa xác định");
        payment.setNotes("success");
        paymentRepository.save(payment);
    }
}

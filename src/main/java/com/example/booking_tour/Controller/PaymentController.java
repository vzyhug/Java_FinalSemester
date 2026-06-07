package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.BookingPaymentDTO;
import com.example.booking_tour.Model.BookingPassenger;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Services.BookingServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private BookingServices bookingServices;

    @PostMapping("/process")
    public String processPayment(@ModelAttribute BookingPaymentDTO dto, HttpSession session, Model model) {

        Map<String, Object> bookingStage1 = (Map<String, Object>) session.getAttribute("bookingStage1");
        @SuppressWarnings("unchecked")
        List<BookingPassenger> bookingPassengers = (List<BookingPassenger>) session.getAttribute("bookingPassengers");
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");

        if (bookingStage1 == null || bookingPassengers == null || loggedInCustomer == null) {
            model.addAttribute("error", "Dữ liệu đặt tour không hợp lệ hoặc phiên giao dịch đã hết hạn.");
            return "redirect:/";
        }

        try {
            bookingServices.saveBookingAndPassengerAndPayment(dto, bookingStage1, bookingPassengers, loggedInCustomer);

            // Xóa session sau khi lưu xong
            session.removeAttribute("bookingStage1");
            session.removeAttribute("bookingPassengers");

            // Chuyển hướng về trang lịch sử thanh toán hoặc trang chủ
            return "redirect:/customer/payment-history";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi lưu thông tin: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/vnpay-return")
    public String paymentReturn(@RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                                HttpSession session, Model model) {

        if (responseCode == null || "00".equals(responseCode)) {

            BookingPaymentDTO bookingData = (BookingPaymentDTO) session.getAttribute("TEMP_BOOKING");

            if (bookingData != null) {

                session.removeAttribute("TEMP_BOOKING");

                model.addAttribute("message", "Thanh toán thành công! Chuyến đi của bạn đã được xác nhận.");
                return "redirect:/"; // Tạm thời redirect về trang chủ, bạn có thể tạo view riêng
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt tour trong phiên giao dịch.");
            }
        } else {
            // Nếu thanh toán thất bại (người dùng hủy, không đủ tiền...)
            model.addAttribute("error", "Thanh toán thất bại hoặc đã bị hủy.");
        }

        return "redirect:/"; // Có thể trả về trang báo lỗi riêng
    }
}

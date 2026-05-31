package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.BookingPaymentDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    // 1. Endpoint nhận dữ liệu Đặt Tour và khởi tạo Thanh toán
    @PostMapping("/process")
    public String processPayment(@ModelAttribute BookingPaymentDTO dto, HttpSession session) {
        
        // LƯU TẠM DỮ LIỆU: Đưa cục DTO vào Session với key "TEMP_BOOKING"
        session.setAttribute("TEMP_BOOKING", dto);
        
        // --- LOGIC GỌI CỔNG THANH TOÁN (VNPAY / MOMO) ---
        // Tại đây bạn sẽ build URL gọi API của cổng thanh toán theo tài liệu của họ
        // Ví dụ: Tạo ra một cái URL chứa mã đơn hàng, số tiền, url trả về (returnUrl)
        String vnpayPaymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..."; 
        
        // (Đây chỉ là URL giả lập, thực tế phải build URL kèm chữ ký số checksum)
        
        // Chuyển hướng người dùng sang cổng thanh toán của bên thứ 3
        return "redirect:" + vnpayPaymentUrl; 
    }

    // 2. Endpoint nhận Callback (Kết quả trả về) từ cổng thanh toán
    @GetMapping("/vnpay-return")
    public String paymentReturn(@RequestParam(value = "vnp_ResponseCode", required = false) String responseCode, 
                                HttpSession session, Model model) {
                                    
        // Giả lập: Nếu không truyền param lên thì cứ cho là thành công ("00")
        if (responseCode == null || "00".equals(responseCode)) {
            
            // LẤY LẠI DỮ LIỆU TẠM: Ép kiểu lại thành DTO đã lưu trước đó
            BookingPaymentDTO bookingData = (BookingPaymentDTO) session.getAttribute("TEMP_BOOKING");
            
            if (bookingData != null) {
                // ==========================================
                // >>> PHẦN ĐỒNG ĐỘI SẼ LÀM VIỆC VỚI DB <<<
                // ==========================================
                // Đồng đội sẽ gọi Service để lưu data vào Database, ví dụ:
                // bookingService.saveBookingAndPassengerAndPayment(bookingData);
                
                // Xóa data trong session sau khi đã lưu xong để giải phóng bộ nhớ
                session.removeAttribute("TEMP_BOOKING");
                
                model.addAttribute("message", "Thanh toán thành công! Chuyến đi của bạn đã được xác nhận.");
                return "redirect:/home"; // Tạm thời redirect về trang chủ, bạn có thể tạo view riêng
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt tour trong phiên giao dịch.");
            }
        } else {
            // Nếu thanh toán thất bại (người dùng hủy, không đủ tiền...)
            model.addAttribute("error", "Thanh toán thất bại hoặc đã bị hủy.");
        }
        
        return "redirect:/home"; // Có thể trả về trang báo lỗi riêng
    }
}

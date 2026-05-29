package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminServices {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TourRepository tourRepository;

    // ==================== AUTHENTICATION ====================
    public Employee adminLogin(String username, String password) {
        try {
            Employee employee = employeeRepository.findByUsername(username);

            // Kiểm tra employee có tồn tại
            if (employee == null) {
                return null;
            }

            // Kiểm tra mật khẩu (hiện tại so sánh trực tiếp, sau này dùng BCrypt)
            if (!employee.getPasswordHash().equals(password)) {
                return null;
            }

            // Kiểm tra employee có hoạt động không
            if (!employee.getIsActive()) {
                return null;
            }

            // Kiểm tra employee có phải admin hoặc nhân viên không
            if (employee.getRole() != null) {
                String roleName = employee.getRole().getRoleName();
                if (roleName.equals("Admin") || roleName.equals("Sale")) {
                    return employee;
                }
            }

            return null;
        } catch (Exception e) {
            System.out.println("Lỗi trong adminLogin: " + e.getMessage());
            return null;
        }
    }

    // ==================== STATISTICS ====================

    /**
     * Lấy tổng doanh thu từ tất cả thanh toán
     */
    public BigDecimal getTotalRevenue() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            BigDecimal total = BigDecimal.ZERO;

            if (payments != null && !payments.isEmpty()) {
                for (Payment payment : payments) {
                    if (payment.getAmount() != null) {
                        total = total.add(payment.getAmount());
                    }
                }
            }

            return total;
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalRevenue: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Lấy tổng số booking
     */
    public Long getTotalBookings() {
        try {
            return bookingRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalBookings: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy tổng số khách hàng
     */
    public Long getTotalCustomers() {
        try {
            return customerRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalCustomers: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy tổng số tour
     */
    public Long getTotalTours() {
        try {
            return tourRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalTours: " + e.getMessage());
            return 0L;
        }
    }

    // ==================== DATA RETRIEVAL ====================

    /**
     * Lấy danh sách booking gần đây
     * @param limit Số lượng booking cần lấy
     */
    public List<Booking> getRecentBookings(int limit) {
        try {
            List<Booking> bookings = bookingRepository.findTop5ByOrderByBookingDateDesc();
            if (bookings != null && bookings.size() > limit) {
                return bookings.subList(0, limit);
            }
            return bookings;
        } catch (Exception e) {
            System.out.println("Lỗi trong getRecentBookings: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy danh sách booking đang chờ xử lý
     */
    public List<Booking> getPendingBookings() {
        try {
            return bookingRepository.findByStatus("pending");
        } catch (Exception e) {
            System.out.println("Lỗi trong getPendingBookings: " + e.getMessage());
            return null;
        }
    }
}

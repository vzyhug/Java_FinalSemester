package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // ==================== THỐNG KÊ TỔNG (ALL-TIME) ====================

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

    public Long getTotalBookings() {
        try {
            return bookingRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalBookings: " + e.getMessage());
            return 0L;
        }
    }

    public Long getTotalCustomers() {
        try {
            return customerRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalCustomers: " + e.getMessage());
            return 0L;
        }
    }

    public Long getTotalTours() {
        try {
            return tourRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalTours: " + e.getMessage());
            return 0L;
        }
    }

    // ==================== THỐNG KÊ THEO KHOẢNG THỜI GIAN (BỘ LỌC) ====================

    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            BigDecimal revenue = bookingRepository.calculateTotalRevenueByDateRange(startDate, endDate);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalRevenueByDateRange: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public Long getTotalBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Long count = bookingRepository.countBookingsByDateRange(startDate, endDate);
            return count != null ? count : 0L;
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalBookingsByDateRange: " + e.getMessage());
            return 0L;
        }
    }

    public Long getTotalCustomersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Long count = customerRepository.countCustomersByDateRange(startDate, endDate);
            return count != null ? count : 0L;
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalCustomersByDateRange: " + e.getMessage());
            return 0L;
        }
    }

    // ==================== DATA RETRIEVAL ====================

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

    public List<Booking> getPendingBookings() {
        try {
            return bookingRepository.findByStatus("pending");
        } catch (Exception e) {
            System.out.println("Lỗi trong getPendingBookings: " + e.getMessage());
            return null;
        }
    }

    // ==================== QUẢN LÝ NHÂN VIÊN / HDV ====================

    public List<Employee> getAllAdmins() {
        try {
            return employeeRepository.findAll();
        } catch (Exception e) {
            System.out.println("Lỗi trong getAllAdmins: " + e.getMessage());
            return null;
        }
    }

    public Employee getAdminById(Integer id) {
        try {
            return employeeRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.out.println("Lỗi trong getAdminById: " + e.getMessage());
            return null;
        }
    }
}
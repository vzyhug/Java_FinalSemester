package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Repository.BookingRepository;
import com.example.booking_tour.Repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class CustomerServices {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;


    @Autowired
    private final PasswordEncoder passwordEncoder;

    // Lưu thông tin khách hàng mới sau khi đăng ký
    @Transactional
    public void register(Customer customer) {
        // Lưu thông tin vào database
        customerRepository.save(customer);
    }

    // đăng nhập tài khoản khách hàng
    public Customer login(String email, String password)
    {
        // Tìm khách hàng trong database
        Customer existingCustomer = customerRepository.findByEmail(email);
        // Nếu tìm thấy khách hàng và mật khẩu khớp thì trả về đối tượng Customer
        if (existingCustomer != null && passwordEncoder.matches(password, existingCustomer.getPasswordHash())) {

                return existingCustomer;
            }
        return null; // Đăng nhập thất bại
    }



    public Long getTotalCustomers() {
        try {
            return customerRepository.count();
        } catch (Exception e) {
            System.out.println("Lỗi trong getTotalCustomers: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy số khách hàng mới trong tháng hiện tại
     */
    public Long getNewCustomersThisMonth() {
        try {
            List<Customer> allCustomers = customerRepository.findAll();
            long count = 0;

            if (allCustomers != null && !allCustomers.isEmpty()) {
                java.time.YearMonth currentMonth = java.time.YearMonth.now();

                for (Customer customer : allCustomers) {
                    if (customer.getCreatedAt() != null) {
                        java.time.YearMonth customerMonth = java.time.YearMonth.from(customer.getCreatedAt());
                        if (customerMonth.equals(currentMonth)) {
                            count++;
                        }
                    }
                }
            }

            return count;
        } catch (Exception e) {
            System.out.println("Lỗi trong getNewCustomersThisMonth: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Tính tỉ lệ khách hàng quay lại (repeat customer rate)
     */
    public Double getReturnRate() {
        try {
            List<Customer> allCustomers = customerRepository.findAll();
            if (allCustomers == null || allCustomers.isEmpty()) {
                return 0.0;
            }

            long repeatCustomerCount = 0;
            List<Booking> allBookings = bookingRepository.findAll();

            for (Customer customer : allCustomers) {
                long customerBookingCount = 0;
                if (allBookings != null) {
                    for (Booking booking : allBookings) {
                        if (booking.getCustomer() != null && booking.getCustomer().getCustomerId()
                                .equals(customer.getCustomerId())) {
                            customerBookingCount++;
                        }
                    }
                }

                if (customerBookingCount >= 2) {
                    repeatCustomerCount++;
                }
            }

            double rate = (repeatCustomerCount * 100.0) / allCustomers.size();
            return Math.round(rate * 10.0) / 10.0;
        } catch (Exception e) {
            System.out.println("Lỗi trong getReturnRate: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Lấy đánh giá trung bình từ customers
     */
    public Double getAverageRating() {
        try {
            // TODO: Implement khi có Review model
            // Hiện tại return giá trị demo
            return 4.8;
        } catch (Exception e) {
            System.out.println("Lỗi trong getAverageRating: " + e.getMessage());
            return 0.0;
        }
    }

    // ==================== DATA RETRIEVAL ====================

    /**
     * Lấy danh sách TẤT CẢ khách hàng
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            System.out.println("Lỗi trong getAllCustomers: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy khách hàng theo ID
     */
    public Customer getCustomerById(Integer customerId) {
        try {
            return customerRepository.findById(customerId).orElse(null);
        } catch (Exception e) {
            System.out.println("Lỗi trong getCustomerById: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tìm khách hàng theo tên
     */
    public List<Customer> searchCustomersByName(String fullName) {
        try {
            if (fullName == null || fullName.trim().isEmpty()) {
                return getAllCustomers();
            }
            return customerRepository.findByFullNameContaining(fullName);
        } catch (Exception e) {
            System.out.println("Lỗi trong searchCustomersByName: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tìm khách hàng theo email
     */
    public Customer getCustomerByEmail(String email) {
        try {
            return customerRepository.findByEmail(email);
        } catch (Exception e) {
            System.out.println("Lỗi trong getCustomerByEmail: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các booking của khách hàng
     */
    public List<Booking> getCustomerBookings(Integer customerId) {
        try {
            return bookingRepository.findByCustomer_CustomerId(customerId);
        } catch (Exception e) {
            System.out.println("Lỗi trong getCustomerBookings: " + e.getMessage());
            return null;
        }
    }
    /**
     * Lấy số lượng tour đã đặt của khách hàng
     */
    public Long getCustomerTourCount(Integer customerId) {
        try {
            List<Booking> bookings = getCustomerBookings(customerId);
            return (long) (bookings != null ? bookings.size() : 0);
        } catch (Exception e) {
            System.out.println("Lỗi trong getCustomerTourCount: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Tính tổng chi tiêu của khách hàng
     */
    public java.math.BigDecimal getCustomerTotalSpent(Integer customerId) {
        try {
            List<Booking> bookings = getCustomerBookings(customerId);
            java.math.BigDecimal totalSpent = java.math.BigDecimal.ZERO;

            if (bookings != null && !bookings.isEmpty()) {
                for (Booking booking : bookings) {
                    if (booking.getTotalAmount() != null) {
                        totalSpent = totalSpent.add(booking.getTotalAmount());
                    }
                }
            }

            return totalSpent;
        } catch (Exception e) {
            System.out.println("Lỗi trong getCustomerTotalSpent: " + e.getMessage());
            return java.math.BigDecimal.ZERO;
        }
    }

    // ==================== CRUD OPERATIONS ====================

    /**
     * Thêm/Cập nhật khách hàng
     */
    public Customer saveCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            System.out.println("Lỗi trong saveCustomer: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xóa khách hàng
     */
    public boolean deleteCustomer(Integer customerId) {
        try {
            customerRepository.deleteById(customerId);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi trong deleteCustomer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Khóa tài khoản khách hàng (không xóa, chỉ khóa)
     */
    public boolean deactivateCustomer(Integer customerId) {
        try {
            // TODO: Implement khi Customer model có field isActive
            // customer.setIsActive(false);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi trong deactivateCustomer: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public void toggleCustomerStatus(Integer customerId) {
        System.out.println("--- BẮT ĐẦU VÀO SERVICE ---");

        // 1. Tìm khách hàng
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer != null) {
            System.out.println("- Đã tìm thấy khách hàng: " + customer.getFullName());
            System.out.println("- Trạng thái cũ trong DB: " + customer.getIsActive());

            // 2. Đổi trạng thái
            boolean currentStatus = customer.getIsActive() != null ? customer.getIsActive() : true;
            customer.setIsActive(!currentStatus);

            System.out.println("- Trạng thái MỚI chuẩn bị lưu: " + customer.getIsActive());

            // 3. Lưu xuống DB
            customerRepository.save(customer);
            System.out.println("- Đã gọi lệnh SAVE thành công!");

        } else {
            System.out.println("- LỖI: Không tìm thấy khách hàng với ID: " + customerId);
            throw new RuntimeException("Không tìm thấy khách hàng trong cơ sở dữ liệu!");
        }

        System.out.println("--- KẾT THÚC SERVICE ---");
    }


    //change password profile
    public void changePassword(Integer customerId, String newPassword) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Mã hóa mật khẩu mới
        String encoded = passwordEncoder.encode(newPassword);
        customer.setPasswordHash(encoded);

        customerRepository.save(customer);
    }

    @Transactional
    public void updateCustomer(Customer customer) {

        customerRepository.save(customer);
    }
}

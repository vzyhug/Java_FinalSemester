package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Bill;
import com.example.booking_tour.Repository.BillRepository;
import com.example.booking_tour.Repository.PaymentRepository;
import com.example.booking_tour.Services.EmployeeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PayManagementController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmployeeServices employeeService;


    @GetMapping("/payment_management")
    public String paymentManagement(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {

        model.addAttribute("admin", employeeService.getCurrentAdmin());
        Pageable pageable = PageRequest.of(page, 10);

        Page<Bill> billPage = keyword.isEmpty()
                ? billRepository.findAll(pageable)
                : billRepository.findByBillNumberContainingIgnoreCase(keyword, pageable);

        // =========================
        // CHECK PAYMENT STATUS
        // =========================
        Map<Integer, String> paymentStatus = new HashMap<>();

        int paidCount = 0;
        int pendingCount = 0;
        int cancelCount = 0;
        int completedCount = 0;

        for (Bill bill : billPage.getContent()) {

            String status = "pending";

            if (bill.getBooking() != null) {

                String bookingStatus =
                        bill.getBooking().getStatus();

                // Đã hủy
                if ("cancelled".equalsIgnoreCase(bookingStatus)) {

                    status = "cancelled";
                    cancelCount++;
                }

                // Hoàn thành
                else if ("completed".equalsIgnoreCase(bookingStatus)) {

                    status = "completed";
                    completedCount++;
                }

                // Có payment => đã thanh toán
                else {

                    boolean paid =
                            paymentRepository.existsByBooking_BookingId(
                                    bill.getBooking().getBookingId()
                            );

                    if (paid) {

                        status = "paid";
                        paidCount++;

                    } else {

                        pendingCount++;
                    }
                }

            } else {

                pendingCount++;
            }

            paymentStatus.put(
                    bill.getBillId(),
                    status
            );
        }

        // Tổng doanh thu
        BigDecimal totalRevenue = billRepository.getTotalRevenue();

        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("billList", billPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", billPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        model.addAttribute("paidCount", paidCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("cancelCount", cancelCount);
        model.addAttribute("completedCount", completedCount);

        return "admin_transaction_management";
    }
}
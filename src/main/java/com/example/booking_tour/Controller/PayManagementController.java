package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Repository.BillRepository;
import com.example.booking_tour.Repository.PaymentRepository;
import com.example.booking_tour.Services.EmployeeServices;
import com.example.booking_tour.Services.PaymentServices;
import com.example.booking_tour.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class PayManagementController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmployeeServices employeeService;

    @Autowired
    private PaymentServices paymentServices;



//    @GetMapping("/payment_management")
//    public String paymentManagement(
//            Model model,
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(value = "status", required = false) String status
//    ) {
//
//        model.addAttribute("admin", employeeService.getCurrentAdmin());
//
//        // Sắp xếp các hóa đơn mới nhất lên đầu tiên
//        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "billDate");
//
//        // 1. Lấy toàn bộ danh sách hóa đơn khớp với từ khóa tìm kiếm (chưa phân trang để đếm trạng thái chính xác)
//        java.util.List<Bill> allBills = keyword.isEmpty()
//                ? billRepository.findAll(sort)
//                : billRepository.findByBillNumberContainingIgnoreCase(keyword, sort);
//
//        // Map lưu trạng thái hiển thị của từng Bill ID
//        Map<Integer, String> paymentStatus = new HashMap<>();
//
//        // Các biến đếm số lượng theo trạng thái trên toàn bộ dữ liệu khớp từ khóa
//        int paidCount = 0;
//        int pendingCount = 0;
//        int cancelCount = 0;
//        int completedCount = 0;
//
//        // Danh sách sau khi đã lọc theo Trạng thái chọn
//        java.util.List<Bill> filteredBills = new java.util.ArrayList<>();
//
//        // 2. Duyệt qua toàn bộ hóa đơn để xác định trạng thái và đếm số lượng thống kê
//        for (Bill bill : allBills) {
//            String billStatus = "pending";
//
//            if (bill.getBooking() != null) {
//                String bookingStatus = bill.getBooking().getStatus();
//
//                // Trạng thái Đã hủy
//                if ("cancelled".equalsIgnoreCase(bookingStatus)) {
//                    billStatus = "cancelled";
//                    cancelCount++;
//                }
//                // Trạng thái Hoàn thành
//                else if ("completed".equalsIgnoreCase(bookingStatus)) {
//                    billStatus = "completed";
//                    completedCount++;
//                }
//                // Đã thanh toán (nếu có thông tin payment trong DB)
//                else {
//                    boolean paid = paymentRepository.existsByBooking_BookingId(
//                            bill.getBooking().getBookingId()
//                    );
//                    if (paid) {
//                        billStatus = "paid";
//                        paidCount++;
//                    } else {
//                        pendingCount++;
//                    }
//                }
//            } else {
//                pendingCount++;
//            }
//
//            paymentStatus.put(bill.getBillId(), billStatus);
//
//            // Thực hiện lọc theo trạng thái nếu người dùng yêu cầu lọc cụ thể
//            if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase(billStatus)) {
//                filteredBills.add(bill);
//            }
//        }
//
//        // 3. Thực hiện phân trang trong bộ nhớ trên danh sách đã lọc
//        int pageSize = 10;
//        int totalBills = filteredBills.size();
//        int totalPages = (int) Math.ceil((double) totalBills / pageSize);
//        if (totalPages == 0) {
//            totalPages = 1;
//        }
//
//        // Kiểm tra giới hạn chỉ số trang
//        if (page < 0) {
//            page = 0;
//        }
//        if (page >= totalPages) {
//            page = totalPages - 1;
//        }
//
//        int start = page * pageSize;
//        int end = Math.min(start + pageSize, totalBills);
//
//        java.util.List<Bill> paginatedBills = new java.util.ArrayList<>();
//        if (start < totalBills) {
//            paginatedBills = filteredBills.subList(start, end);
//        }
//
//        // 4. Lấy tổng doanh thu thực tế
//        BigDecimal totalRevenue = billRepository.getTotalRevenue();
//
//        // 5. Đổ toàn bộ dữ liệu ra Model sang phía giao diện HTML
//        model.addAttribute("paymentStatus", paymentStatus);
//        model.addAttribute("totalRevenue", totalRevenue);
//        model.addAttribute("billList", paginatedBills);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("status", status); // Truyền trạng thái lọc hiện tại để giữ trạng thái giao diện
//
//        model.addAttribute("paidCount", paidCount);
//        model.addAttribute("pendingCount", pendingCount);
//        model.addAttribute("cancelCount", cancelCount);
//        model.addAttribute("completedCount", completedCount);
//
//        return "admin_transaction_management";
//    }

    @GetMapping("/payment_management")
    public String paymentManagement(
            Model model,
            @RequestParam(defaultValue = "0") int page
    ) {

        model.addAttribute("admin",
                employeeService.getCurrentAdmin());

        Page<Payment> paymentPage =
                paymentServices.findAll(page);

//        BigDecimal totalRevenue =
//                paymentPage.getContent()
//                        .stream()
//                        .map(Payment::getAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        model.addAttribute("paymentList",
                paymentPage.getContent());

        model.addAttribute("currentPage",
                page);

        model.addAttribute("totalPages",
                paymentPage.getTotalPages());

        model.addAttribute("totalRevenue",
                totalRevenue);

        model.addAttribute("paidCount",
                paymentPage.getTotalElements());

        return "admin_transaction_management";
    }
}
package com.example.booking_tour.Controller;


import com.example.booking_tour.Model.Bill;
import com.example.booking_tour.Model.Payment;
import com.example.booking_tour.Services.BillServices;

import com.example.booking_tour.Services.PaymentServices;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillServices billService;
    private final PaymentServices paymentService;

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadBill(
            @PathVariable Integer id) {

        try {

            // Lấy bill/payment
            Bill bill = billService.getBillByBookingId(id);

            Payment payment =
                    paymentService.getPaymentByBookingId(id);

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, out);

            document.open();

            // ===== TIÊU ĐỀ =====
            document.add(new Paragraph("=========== HÓA ĐƠN THANH TOÁN ==========="));
            document.add(new Paragraph(" "));

            // ===== THÔNG TIN =====
            document.add(new Paragraph(
                    "Mã giao dịch: PAY-" + payment.getPaymentId()));

            document.add(new Paragraph(
                    "Tên tour: " +
                            payment.getBooking()
                                    .getDeparture()
                                    .getTour()
                                    .getTitle()));

            document.add(new Paragraph(
                    "Giá tiền: " +
                            payment.getAmount() + " VND"));

            document.add(new Paragraph(
                    "Ngày thanh toán: " +
                            payment.getPaymentDate()));

            document.add(new Paragraph(
                    "Phương thức: " +
                            payment.getPaymentMethod()));

            document.add(new Paragraph(
                    "Trạng thái: " +
                            (payment.getNotes() == null
                                    ? "Đang xử lý"
                                    : payment.getNotes())));

            document.add(new Paragraph(
                    "Trạng thái: " +
                            (payment.getNotes() == "success"
                                    ? "Đã thanh toán"
                                    : payment.getNotes())));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Cảm ơn quý khách đã sử dụng dịch vụ!"));

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=hoadon_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }
}

package com.example.booking_tour.Controller;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Security.JwtUtils;
import com.example.booking_tour.Services.CustomerServices;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final CustomerServices customerService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String email = loginData.get("username"); // Trong JS tôi gửi "username" đại diện cho email
        String password = loginData.get("password");

        Customer loggedInCustomer = customerService.login(email, password);
        
        if (loggedInCustomer != null) {
            // 1. LƯU SESSION ĐỂ THYMELEAF HOẠT ĐỘNG (Trang chủ hiển thị tên)
            session.setAttribute("loggedInCustomer", loggedInCustomer);
            
            // 2. TẠO JWT TOKEN ĐỂ FRONTEND LƯU LOCALSTORAGE (Quản lý 20 phút)
            String token = jwtUtils.generateToken(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInCustomer);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Sai tài khoản hoặc mật khẩu"));
        }
    }
}

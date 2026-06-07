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
        String email = loginData.get("username");
        String password = loginData.get("password");

        Customer loggedInCustomer = customerService.login(email, password);
        
        if (loggedInCustomer != null) {
            // LƯU SESSION ĐỂ THYMELEAF HOẠT ĐỘNG
            session.setAttribute("loggedInCustomer", loggedInCustomer);
            
            // .TẠO JWT TOKEN ĐỂ FRONTEND LƯU LOCALSTORAGE
            String token = jwtUtils.generateToken(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInCustomer);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Sai tài khoản hoặc mật khẩu"));
        }
    }

    // Tự động khôi phục Session
    @PostMapping("/restore-session")
    public ResponseEntity<?> restoreSession(@RequestHeader(value = "Authorization", required = false) String authHeader, HttpSession session) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing token");
        }

        String token = authHeader.substring(7);
        if (jwtUtils.validateToken(token)) {
            String email = jwtUtils.getEmailFromToken(token);
            Customer customer = customerService.findByEmail(email);

            if(customer != null) {
               session.setAttribute("loggedInCustomer", customer);
               return ResponseEntity.ok(Map.of("message", "Khôi phục thành công"));
            }
        }
        return ResponseEntity.status(401).body("Invalid token");
    }
}

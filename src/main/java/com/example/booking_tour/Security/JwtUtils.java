package com.example.booking_tour.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Khóa bí mật (SECRET KEY)
    private static final String SECRET_KEY_STRING = "VeRyS3cr3tK3yVeRyS3cr3tK3yVeRyS3cr3tK3y123!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // CẤU HÌNH THỜI GIAN HẾT HẠN: Chính xác 20 phút (20 * 60 * 1000 mili-giây)
    private static final long JWT_EXPIRATION_MS = 1200000;

    // Hàm tạo Token
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Hàm kiểm tra Token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("Lỗi: Token đã hết hạn (Quá 20 phút)!");
        } catch (MalformedJwtException ex) {
            System.out.println("Lỗi: Token bị sai định dạng!");
        } catch (SecurityException | IllegalArgumentException ex) {
            System.out.println("Lỗi: Token không hợp lệ (Bị hacker sửa đổi)!");
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

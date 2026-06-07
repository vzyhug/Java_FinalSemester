package com.example.booking_tour.Model;

import jakarta.persistence.*;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;


@Data
@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Họ và tên không được để trống")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số điện thoại chỉ được chứa số")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(name = "address")
    private String address;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

}
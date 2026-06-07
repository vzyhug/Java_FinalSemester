
package com.example.booking_tour.Model;

import com.example.booking_tour.Model.Role;
import jakarta.persistence.*;
import lombok.Data;


import jakarta.validation.constraints.*;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Họ và tên không được để trống")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số điện thoại chỉ được chứa số")
    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

}


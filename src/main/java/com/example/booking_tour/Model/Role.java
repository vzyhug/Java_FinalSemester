package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName; // Admin, Sale, Guide

    @Column(name = "description")
    private String description;
}


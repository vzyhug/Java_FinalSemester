package com.example.booking_tour.Model;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "provinces")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "province_name", nullable = false, unique = true, length = 50)
    private String provinceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    public enum Region {
        Bắc, Trung, Nam
    }
}
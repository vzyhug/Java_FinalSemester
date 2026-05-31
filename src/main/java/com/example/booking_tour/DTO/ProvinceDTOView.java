package com.example.booking_tour.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProvinceDTOView {
    private Integer provinceId;
    private String provinceName;
    private String region;
    private Integer totalTours;
}

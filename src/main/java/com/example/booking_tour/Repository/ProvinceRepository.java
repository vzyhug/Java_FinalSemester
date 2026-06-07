package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Province;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    boolean existsByProvinceName(String provinceName);
}

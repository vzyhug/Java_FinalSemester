package com.example.booking_tour.Repository;

import com.example.booking_tour.Model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
    /**
     * Tìm tour theo tên (có chứa keyword)
     */
    List<Tour> findByTitleContaining(String keyword);

    /**
     * ĐÃ SỬA: Thêm dấu gạch dưới (_) để map chính xác thuộc tính categoryId
     * bên trong thực thể TourCategory liên kết.
     */
    List<Tour> findByCategory_CategoryId(Integer categoryId);

    /**
     * ĐÃ SỬA: Thêm dấu gạch dưới (_) để map chính xác thuộc tính provinceId
     * bên trong thực thể Province liên kết.
     */
    List<Tour> findByProvince_ProvinceId(Integer provinceId);

    /**
     * Tìm tour theo trạng thái hoạt động
     */
    List<Tour> findByIsActive(Boolean isActive);
}
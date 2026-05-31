package com.example.booking_tour.Repository;


import com.example.booking_tour.Model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.booking_tour.Model.Province;
import org.springframework.data.jpa.repository.EntityGraph;


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

    //================= lay ra danh sach tour ==================
    @Override
    @EntityGraph(attributePaths = {"images"})
    public List<Tour> findAll();


    @EntityGraph(attributePaths = {"images"})
    public List<Tour> findTop4By();
    //================= tim kiem ==================
    //Tim tour theo loai tour
    public List<Tour> findByCategory(int categoryId);
    //Tim tour theo tinh thanh
    public List<Tour> findByProvince(Province province);
    //Tim tour theo so ngay di
    public List<Tour> findByDurationDays(int day);
    //Tim tour theo pickup point
    public List<Tour> findByPickupPoint(String pickupPoint);
    //===================== sort danh sach ===================
    //---- sort asc
    public List<Tour> findAllByOrderByMinPriceAsc();
    //---- sort desc
    public List<Tour> findAllByOrderByMinPriceDesc();
}

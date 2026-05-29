package com.example.booking_tour.Repository;
import com.example.booking_tour.Model.Province;
import com.example.booking_tour.Model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {

    List<Tour> findTop6ByOrderByTourIdDesc();

    Integer countByProvince(Province province);

    Page<Tour> findAll(Pageable pageable);

    Page<Tour> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    //List<Tour> findByTitleContaining(String keyword);

    List<Tour> findByCategory_CategoryId(Integer categoryId);

    List<Tour> findByProvince_ProvinceId(Integer provinceId);

    List<Tour> findByIsActive(Boolean isActive);

    List<Tour> findTop10ByOrderByTourIdDesc();

    long countByProvince_ProvinceId(Integer provinceId);

    //================= lay ra danh sach tour ==================
    @Override
    @EntityGraph(attributePaths = {"images"})
    public List<Tour> findAll();


    @EntityGraph(attributePaths = {"images"})
    public List<Tour> findTop4By();

    //================= tim kiem ==================
    //Tim ten tour
    public List<Tour> findByTitleContaining(String title);

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

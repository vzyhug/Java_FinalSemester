package com.example.booking_tour.Repository;



import com.example.booking_tour.Model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.booking_tour.Model.Province;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {



    List<Tour> findTop6ByOrderByTourIdDesc();

    Integer countByProvince(Province province);

    Page<Tour> findAll(Pageable pageable);

    Page<Tour> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    List<Tour> findByCategory_CategoryId(Integer categoryId);

    List<Tour> findByProvince_ProvinceId(Integer provinceId);

    List<Tour> findByIsActive(Boolean isActive);

    List<Tour> findTop10ByOrderByTourIdDesc();

    long countByProvince_ProvinceId(Integer provinceId);

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Tour> findAll();

    @EntityGraph(attributePaths = {"images"})
    List<Tour> findTop4By();

    List<Tour> findByTitleContaining(String title);

    List<Tour> findByProvince(Province province);

    List<Tour> findByDurationDays(int day);

    List<Tour> findByPickupPoint(String pickupPoint);

    List<Tour> findAllByOrderByMinPriceAsc();

    List<Tour> findAllByOrderByMinPriceDesc();


}

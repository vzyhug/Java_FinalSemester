package com.example.booking_tour.Services;

import com.example.booking_tour.Model.Booking;
import com.example.booking_tour.Model.TourDeparture;
import com.example.booking_tour.Repository.BookingRepository;
import com.example.booking_tour.Repository.TourDepartureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartureServices {
    @Autowired
    private TourDepartureRepository tourDepartureRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ==================== STATISTICS ====================

    /**
     * Lấy số chuyến đi đang khởi hành (on-trip)
     */
    public Long getOnGoingDepartures() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();
            long count = 0;

            if (allDepartures != null && !allDepartures.isEmpty()) {
                for (TourDeparture departure : allDepartures) {
                    // Nếu chuyến đi bắt đầu từ trước hoặc hôm nay
                    // VÀ kết thúc sau hoặc hôm nay
                    if ((departure.getDepartureDate().isBefore(today) || departure.getDepartureDate().isEqual(today)) &&
                            (departure.getReturnDate().isAfter(today) || departure.getReturnDate().isEqual(today))) {
                        count++;
                    }
                }
            }

            return count;
        } catch (Exception e) {
            System.out.println("Lỗi trong getOnGoingDepartures: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy số chuyến chờ xác nhận hướng dẫn viên
     */
    public Long getPendingGuideDepartures() {
        try {
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();
            long count = 0;

            if (allDepartures != null && !allDepartures.isEmpty()) {
                for (TourDeparture departure : allDepartures) {
                    // Nếu chưa có guide gán hoặc status = pending
                    if (departure.getGuide() == null) {
                        count++;
                    }
                }
            }

            return count;
        } catch (Exception e) {
            System.out.println("Lỗi trong getPendingGuideDepartures: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy tổng số khách trong ngày hôm nay
     */
    public Long getTodayPassengers() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> todayDepartures = tourDepartureRepository.findAll();

            // Lọc chuyến đi hôm nay
            List<TourDeparture> filteredDepartures = todayDepartures.stream()
                    .filter(d -> d.getDepartureDate().isEqual(today))
                    .collect(Collectors.toList());

            long totalPassengers = 0;

            // Lấy booking của mỗi chuyến
            for (TourDeparture departure : filteredDepartures) {
                List<Booking> bookings = bookingRepository.findByDeparture_DepartureId(departure.getDepartureId());
                if (bookings != null && !bookings.isEmpty()) {
                    for (Booking booking : bookings) {
                        totalPassengers += (booking.getTotalAdults() != null ? booking.getTotalAdults() : 0);
                    }
                }
            }

            return totalPassengers;
        } catch (Exception e) {
            System.out.println("Lỗi trong getTodayPassengers: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Lấy tháng khởi hành sắp tới
     */
    public String getUpcomingDepartureMonth() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();

            if (allDepartures != null && !allDepartures.isEmpty()) {
                // Tìm chuyến đi sớm nhất trong tương lai
                TourDeparture firstFutureDeparture = allDepartures.stream()
                        .filter(d -> d.getDepartureDate().isAfter(today))
                        .min((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                        .orElse(null);

                if (firstFutureDeparture != null) {
                    LocalDate departureDate = firstFutureDeparture.getDepartureDate();
                    return String.format("Tháng %d, %d",
                            departureDate.getMonthValue(),
                            departureDate.getYear());
                }
            }

            return "Tháng này";
        } catch (Exception e) {
            System.out.println("Lỗi trong getUpcomingDepartureMonth: " + e.getMessage());
            return "Tháng này";
        }
    }

    // ==================== DATA RETRIEVAL ====================

    /**
     * Lấy danh sách TẤT CẢ chuyến đi
     */
    public List<TourDeparture> getAllDepartures() {
        try {
            return tourDepartureRepository.findAll();
        } catch (Exception e) {
            System.out.println("Lỗi trong getAllDepartures: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy chuyến đi theo ID
     */
    public TourDeparture getDepartureById(Integer departureId) {
        try {
            return tourDepartureRepository.findById(departureId).orElse(null);
        } catch (Exception e) {
            System.out.println("Lỗi trong getDepartureById: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các chuyến đi của một tour
     */
    public List<TourDeparture> getDeparturesByTour(Integer tourId) {
        try {
            // ĐÃ SỬA: Gọi đúng findByTour_TourId để map chính xác với Tour.java
            return tourDepartureRepository.findByTour_TourId(tourId);
        } catch (Exception e) {
            System.out.println("Lỗi trong getDeparturesByTour: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy chuyến đi theo trạng thái
     */
    public List<TourDeparture> getDeparturesByStatus(String status) {
        try {
            return tourDepartureRepository.findByStatus(status);
        } catch (Exception e) {
            System.out.println("Lỗi trong getDeparturesByStatus: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các chuyến đi sắp tới (upcoming)
     */
    public List<TourDeparture> getUpcomingDepartures() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();

            if (allDepartures != null && !allDepartures.isEmpty()) {
                return allDepartures.stream()
                        .filter(d -> d.getDepartureDate().isAfter(today))
                        .collect(Collectors.toList());
            }

            return null;
        } catch (Exception e) {
            System.out.println("Lỗi trong getUpcomingDepartures: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các chuyến đi đang diễn ra (on-trip)
     */
    public List<TourDeparture> getOnGoingDeparturesList() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();

            if (allDepartures != null && !allDepartures.isEmpty()) {
                return allDepartures.stream()
                        .filter(d -> (d.getDepartureDate().isBefore(today) || d.getDepartureDate().isEqual(today)) &&
                                (d.getReturnDate().isAfter(today) || d.getReturnDate().isEqual(today)))
                        .collect(Collectors.toList());
            }

            return null;
        } catch (Exception e) {
            System.out.println("Lỗi trong getOnGoingDeparturesList: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các chuyến đi đã hoàn thành (completed)
     */
    public List<TourDeparture> getCompletedDepartures() {
        try {
            LocalDate today = LocalDate.now();
            List<TourDeparture> allDepartures = tourDepartureRepository.findAll();

            if (allDepartures != null && !allDepartures.isEmpty()) {
                return allDepartures.stream()
                        .filter(d -> d.getReturnDate().isBefore(today))
                        .collect(Collectors.toList());
            }

            return null;
        } catch (Exception e) {
            System.out.println("Lỗi trong getCompletedDepartures: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy các booking của một chuyến đi
     */
    public List<Booking> getDepartureBookings(Integer departureId) {
        try {
            return bookingRepository.findByDeparture_DepartureId(departureId);
        } catch (Exception e) {
            System.out.println("Lỗi trong getDepartureBookings: " + e.getMessage());
            return null;
        }
    }

    // ==================== CRUD OPERATIONS ====================

    /**
     * Thêm/Cập nhật chuyến đi
     */
    public TourDeparture saveDeparture(TourDeparture departure) {
        try {
            return tourDepartureRepository.save(departure);
        } catch (Exception e) {
            System.out.println("Lỗi trong saveDeparture: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xóa chuyến đi
     */
    public boolean deleteDeparture(Integer departureId) {
        try {
            tourDepartureRepository.deleteById(departureId);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi trong deleteDeparture: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hủy chuyến đi (soft delete)
     */
    /**
     * Hủy chuyến đi (chuyển trạng thái sang cancelled)
     */
    public boolean cancelDeparture(Integer departureId) {
        try {
            // Đã sửa 'departureRepository' thành 'tourDepartureRepository'
            TourDeparture departure = tourDepartureRepository.findById(departureId).orElse(null);

            if (departure != null) {
                // Đổi trạng thái thành 'cancelled' (đã hủy)
                departure.setStatus("cancelled");
                // Lưu lại vào Database
                tourDepartureRepository.save(departure);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Lỗi khi hủy chuyến: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gán hướng dẫn viên cho chuyến đi
     */
    public boolean assignGuide(Integer departureId, Integer guideId) {
        try {
            TourDeparture departure = getDepartureById(departureId);
            if (departure != null) {
                // TODO: Lấy Employee object từ employeeRepository
                // departure.setGuide(guide);
                tourDepartureRepository.save(departure);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Lỗi trong assignGuide: " + e.getMessage());
            return false;
        }
    }


    public List<TourDeparture> searchDepartures(String keyword, LocalDate date) {
        return tourDepartureRepository.searchDepartures(keyword, date);
    }
    public List<TourDeparture> getDeparturesByMonthAndYear(int month, int year) {
        return tourDepartureRepository.getDeparturesByMonthAndYear(month, year);
    }
    public List<TourDeparture> getPendingGuideDeparturesList() {
        return tourDepartureRepository.findByGuideIsNull();
    }
}

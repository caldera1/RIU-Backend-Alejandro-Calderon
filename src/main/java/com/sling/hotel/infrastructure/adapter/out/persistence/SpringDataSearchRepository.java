package com.sling.hotel.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataSearchRepository extends JpaRepository<SearchEntity, Long> {

    Optional<SearchEntity> findFirstBySearchId(String searchId);

    long countByHotelIdAndCheckInAndCheckOutAndAges(
            String hotelId, LocalDate checkIn, LocalDate checkOut, String ages);
}

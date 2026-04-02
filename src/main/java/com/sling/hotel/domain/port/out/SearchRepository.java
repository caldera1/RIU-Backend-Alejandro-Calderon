package com.sling.hotel.domain.port.out;

import com.sling.hotel.domain.model.HotelSearch;

import java.util.Optional;

public interface SearchRepository {

    void save(String searchId, HotelSearch hotelSearch);

    Optional<HotelSearch> findBySearchId(String searchId);

    long countBySearchId(String searchId);
}

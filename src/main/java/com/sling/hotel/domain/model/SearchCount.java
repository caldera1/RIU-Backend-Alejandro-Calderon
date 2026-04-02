package com.sling.hotel.domain.model;

public record SearchCount(
        String searchId,
        HotelSearch search,
        long count
) {
}

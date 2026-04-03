package com.sling.hotel.infrastructure.adapter.in.rest;

import java.util.List;

public record CountResponse(
        String searchId,
        SearchDetail search,
        long count
) {

    public record SearchDetail(
            String hotelId,
            String checkIn,
            String checkOut,
            List<Integer> ages
    ) {
    }
}

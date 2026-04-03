package com.sling.hotel.infrastructure.adapter.in.rest;

import java.util.List;

public record SearchRequest(
        String hotelId,
        String checkIn,
        String checkOut,
        List<Integer> ages
) {
}

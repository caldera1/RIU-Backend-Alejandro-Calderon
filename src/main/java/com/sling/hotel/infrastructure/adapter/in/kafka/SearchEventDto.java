package com.sling.hotel.infrastructure.adapter.in.kafka;

import java.time.LocalDate;
import java.util.List;

public record SearchEventDto(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {
}

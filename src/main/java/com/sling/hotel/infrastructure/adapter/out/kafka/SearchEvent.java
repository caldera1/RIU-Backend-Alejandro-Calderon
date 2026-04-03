package com.sling.hotel.infrastructure.adapter.out.kafka;

import java.time.LocalDate;
import java.util.List;

public record SearchEvent(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {
}

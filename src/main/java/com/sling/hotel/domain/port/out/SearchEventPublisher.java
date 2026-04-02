package com.sling.hotel.domain.port.out;

import com.sling.hotel.domain.model.HotelSearch;

public interface SearchEventPublisher {

    void publish(String searchId, HotelSearch hotelSearch);
}

package com.sling.hotel.application.port.in;

import com.sling.hotel.domain.model.HotelSearch;

public interface SearchUseCase {

    String search(HotelSearch hotelSearch);
}

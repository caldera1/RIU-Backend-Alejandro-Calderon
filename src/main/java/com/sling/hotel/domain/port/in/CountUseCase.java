package com.sling.hotel.domain.port.in;

import com.sling.hotel.domain.model.SearchCount;

public interface CountUseCase {

    SearchCount count(String searchId);
}

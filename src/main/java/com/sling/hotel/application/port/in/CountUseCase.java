package com.sling.hotel.application.port.in;

import com.sling.hotel.domain.model.SearchCount;

public interface CountUseCase {

    SearchCount count(String searchId);
}

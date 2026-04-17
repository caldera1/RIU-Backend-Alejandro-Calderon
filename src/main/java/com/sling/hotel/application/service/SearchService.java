package com.sling.hotel.application.service;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.model.SearchCount;
import com.sling.hotel.application.port.in.CountUseCase;
import com.sling.hotel.application.port.in.SearchUseCase;
import com.sling.hotel.domain.port.out.SearchEventPublisher;
import com.sling.hotel.domain.port.out.SearchRepository;

import java.util.UUID;

public class SearchService implements SearchUseCase, CountUseCase {

    private final SearchEventPublisher eventPublisher;
    private final SearchRepository repository;

    public SearchService(SearchEventPublisher eventPublisher, SearchRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    @Override
    public String search(HotelSearch hotelSearch) {
        var searchId = UUID.randomUUID().toString();
        eventPublisher.publish(searchId, hotelSearch);
        return searchId;
    }

    @Override
    public SearchCount count(String searchId) {
        var hotelSearch = repository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));
        var count = repository.countBySearchId(searchId);
        return new SearchCount(searchId, hotelSearch, count);
    }
}

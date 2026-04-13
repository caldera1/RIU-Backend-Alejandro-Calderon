package com.sling.hotel.infrastructure.adapter.out.persistence;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.port.out.SearchRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SearchPersistenceAdapter implements SearchRepository {

    private final SpringDataSearchRepository jpaRepository;

    public SearchPersistenceAdapter(SpringDataSearchRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(String searchId, HotelSearch hotelSearch) {
        var entity = new SearchEntity();
        entity.setSearchId(searchId);
        entity.setHotelId(hotelSearch.hotelId());
        entity.setCheckIn(hotelSearch.checkIn());
        entity.setCheckOut(hotelSearch.checkOut());
        entity.setAges(agesToString(hotelSearch.ages()));
        jpaRepository.save(entity);
    }

    @Override
    public Optional<HotelSearch> findBySearchId(String searchId) {
        return jpaRepository.findFirstBySearchId(searchId)
                .map(this::toDomain);
    }

    @Override
    public long countBySearchId(String searchId) {
        return jpaRepository.findFirstBySearchId(searchId)
                .map(entity -> jpaRepository.countByParams(
                        entity.getHotelId(), entity.getCheckIn(), entity.getCheckOut(), entity.getAges()))
                .orElse(0L);
    }

    private HotelSearch toDomain(SearchEntity entity) {
        return new HotelSearch(
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                parseAges(entity.getAges()));
    }

    private String agesToString(List<Integer> ages) {
        return ages.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private List<Integer> parseAges(String ages) {
        return Arrays.stream(ages.split(","))
                .map(Integer::parseInt)
                .toList();
    }
}

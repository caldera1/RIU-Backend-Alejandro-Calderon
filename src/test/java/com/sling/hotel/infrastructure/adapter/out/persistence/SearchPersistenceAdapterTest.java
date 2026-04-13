package com.sling.hotel.infrastructure.adapter.out.persistence;

import com.sling.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPersistenceAdapterTest {

    @Mock
    private SpringDataSearchRepository jpaRepository;

    @InjectMocks
    private SearchPersistenceAdapter adapter;

    @Test
    void saveShouldPersistEntity() {
        var hotelSearch = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1));

        adapter.save("abc-123", hotelSearch);

        var captor = ArgumentCaptor.forClass(SearchEntity.class);
        verify(jpaRepository).save(captor.capture());

        var entity = captor.getValue();
        assertThat(entity.getSearchId()).isEqualTo("abc-123");
        assertThat(entity.getHotelId()).isEqualTo("H1");
        assertThat(entity.getCheckIn()).isEqualTo(LocalDate.of(2023, 12, 29));
        assertThat(entity.getCheckOut()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(entity.getAges()).isEqualTo("30,29,1");
    }

    @Test
    void findBySearchIdShouldReturnDomainModel() {
        var entity = buildEntity("abc-123", "H1", "30,29");
        when(jpaRepository.findFirstBySearchId("abc-123")).thenReturn(Optional.of(entity));

        var result = adapter.findBySearchId("abc-123");

        assertThat(result).isPresent();
        var search = result.get();
        assertThat(search.hotelId()).isEqualTo("H1");
        assertThat(search.ages()).containsExactly(30, 29);
    }

    @Test
    void findBySearchIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findFirstBySearchId("unknown")).thenReturn(Optional.empty());

        assertThat(adapter.findBySearchId("unknown")).isEmpty();
    }

    @Test
    void countBySearchIdShouldCountMatchingSearches() {
        var entity = buildEntity("abc-123", "H1", "30,29");
        when(jpaRepository.findFirstBySearchId("abc-123")).thenReturn(Optional.of(entity));
        when(jpaRepository.countByParams(
                "H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), "30,29"))
                .thenReturn(3L);

        assertThat(adapter.countBySearchId("abc-123")).isEqualTo(3);
    }

    @Test
    void countBySearchIdShouldReturnZeroWhenNotFound() {
        when(jpaRepository.findFirstBySearchId("unknown")).thenReturn(Optional.empty());

        assertThat(adapter.countBySearchId("unknown")).isZero();
    }

    private SearchEntity buildEntity(String searchId, String hotelId, String ages) {
        var entity = new SearchEntity();
        entity.setSearchId(searchId);
        entity.setHotelId(hotelId);
        entity.setCheckIn(LocalDate.of(2023, 12, 29));
        entity.setCheckOut(LocalDate.of(2023, 12, 31));
        entity.setAges(ages);
        return entity;
    }
}

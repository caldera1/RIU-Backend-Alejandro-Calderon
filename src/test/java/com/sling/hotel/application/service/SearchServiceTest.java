package com.sling.hotel.application.service;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.port.out.SearchEventPublisher;
import com.sling.hotel.domain.port.out.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchEventPublisher eventPublisher;

    @Mock
    private SearchRepository repository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(eventPublisher, repository);
    }

    @Test
    void searchShouldReturnUniqueId() {
        var hotelSearch = buildSearch();

        var id1 = searchService.search(hotelSearch);
        var id2 = searchService.search(hotelSearch);

        assertThat(id1).isNotBlank();
        assertThat(id2).isNotBlank();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void searchShouldPublishEvent() {
        var hotelSearch = buildSearch();

        var searchId = searchService.search(hotelSearch);

        var idCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publish(idCaptor.capture(), eq(hotelSearch));
        assertThat(idCaptor.getValue()).isEqualTo(searchId);
    }

    @Test
    void countShouldReturnSearchCount() {
        var hotelSearch = buildSearch();
        when(repository.findBySearchId("id-1")).thenReturn(Optional.of(hotelSearch));
        when(repository.countBySearchId("id-1")).thenReturn(3L);

        var result = searchService.count("id-1");

        assertThat(result.searchId()).isEqualTo("id-1");
        assertThat(result.search()).isEqualTo(hotelSearch);
        assertThat(result.count()).isEqualTo(3);
    }

    @Test
    void countShouldThrowWhenNotFound() {
        when(repository.findBySearchId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchService.count("unknown"))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    private HotelSearch buildSearch() {
        return new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29));
    }
}

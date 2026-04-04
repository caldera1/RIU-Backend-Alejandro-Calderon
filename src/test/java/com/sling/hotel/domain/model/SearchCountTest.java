package com.sling.hotel.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchCountTest {

    @Test
    void shouldHoldSearchAndCount() {
        var search = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30));
        var searchCount = new SearchCount("abc-123", search, 5);

        assertThat(searchCount.searchId()).isEqualTo("abc-123");
        assertThat(searchCount.search()).isEqualTo(search);
        assertThat(searchCount.count()).isEqualTo(5);
    }
}

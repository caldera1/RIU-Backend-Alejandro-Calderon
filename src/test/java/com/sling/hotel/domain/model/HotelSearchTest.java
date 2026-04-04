package com.sling.hotel.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HotelSearchTest {

    @Test
    void shouldCreateWithCorrectFields() {
        var search = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29));

        assertThat(search.hotelId()).isEqualTo("H1");
        assertThat(search.checkIn()).isEqualTo(LocalDate.of(2023, 12, 29));
        assertThat(search.checkOut()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(search.ages()).containsExactly(30, 29);
    }

    @Test
    void shouldMakeDefensiveCopyOfAges() {
        var mutableAges = new ArrayList<>(List.of(30, 29, 1));
        var search = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), mutableAges);

        mutableAges.add(99);

        assertThat(search.ages()).containsExactly(30, 29, 1);
    }

    @Test
    void shouldReturnImmutableAgesList() {
        var search = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29));

        assertThatThrownBy(() -> search.ages().add(5))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldPreserveAgesOrder() {
        var search1 = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1));
        var search2 = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(1, 29, 30));

        assertThat(search1.ages()).containsExactly(30, 29, 1);
        assertThat(search2.ages()).containsExactly(1, 29, 30);
        assertThat(search1).isNotEqualTo(search2);
    }
}

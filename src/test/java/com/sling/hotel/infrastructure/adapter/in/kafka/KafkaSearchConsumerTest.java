package com.sling.hotel.infrastructure.adapter.in.kafka;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.port.out.SearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaSearchConsumerTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private KafkaSearchConsumer consumer;

    @Test
    void shouldPersistEventAsHotelSearch() {
        var event = new SearchEventDto(
                "abc-123", "H1",
                LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31),
                List.of(30, 29));

        consumer.consume(event);

        var captor = ArgumentCaptor.forClass(HotelSearch.class);
        verify(searchRepository).save(eq("abc-123"), captor.capture());

        var saved = captor.getValue();
        assertThat(saved.hotelId()).isEqualTo("H1");
        assertThat(saved.checkIn()).isEqualTo(LocalDate.of(2023, 12, 29));
        assertThat(saved.checkOut()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(saved.ages()).containsExactly(30, 29);
    }
}

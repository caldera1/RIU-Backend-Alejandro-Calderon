package com.sling.hotel.infrastructure.adapter.out.kafka;

import com.sling.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaSearchProducerTest {

    @Mock
    private KafkaTemplate<String, SearchEvent> kafkaTemplate;

    @Test
    void shouldSendEventToTopic() {
        var producer = new KafkaSearchProducer(kafkaTemplate, "test-topic");
        var hotelSearch = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29));

        producer.publish("abc-123", hotelSearch);

        var eventCaptor = ArgumentCaptor.forClass(SearchEvent.class);
        verify(kafkaTemplate).send(eq("test-topic"), eq("abc-123"), eventCaptor.capture());

        var event = eventCaptor.getValue();
        assertThat(event.searchId()).isEqualTo("abc-123");
        assertThat(event.hotelId()).isEqualTo("H1");
        assertThat(event.checkIn()).isEqualTo(LocalDate.of(2023, 12, 29));
        assertThat(event.checkOut()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(event.ages()).containsExactly(30, 29);
    }
}

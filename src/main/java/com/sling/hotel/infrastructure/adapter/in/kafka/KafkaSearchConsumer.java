package com.sling.hotel.infrastructure.adapter.in.kafka;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.port.out.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaSearchConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaSearchConsumer.class);

    private final SearchRepository searchRepository;

    public KafkaSearchConsumer(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @KafkaListener(topics = "${kafka.topic.searches}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(SearchEventDto event) {
        log.info("Received search event: {}", event.searchId());
        var hotelSearch = new HotelSearch(
                event.hotelId(),
                event.checkIn(),
                event.checkOut(),
                event.ages());
        searchRepository.save(event.searchId(), hotelSearch);
    }
}

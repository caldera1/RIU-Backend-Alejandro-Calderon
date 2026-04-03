package com.sling.hotel.infrastructure.adapter.out.kafka;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.port.out.SearchEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSearchProducer implements SearchEventPublisher {

    private final KafkaTemplate<String, SearchEvent> kafkaTemplate;
    private final String topic;

    public KafkaSearchProducer(
            KafkaTemplate<String, SearchEvent> kafkaTemplate,
            @Value("${kafka.topic.searches}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(String searchId, HotelSearch hotelSearch) {
        var event = new SearchEvent(
                searchId,
                hotelSearch.hotelId(),
                hotelSearch.checkIn(),
                hotelSearch.checkOut(),
                hotelSearch.ages());
        kafkaTemplate.send(topic, searchId, event);
    }
}

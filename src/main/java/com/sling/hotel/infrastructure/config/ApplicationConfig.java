package com.sling.hotel.infrastructure.config;

import com.sling.hotel.application.service.SearchService;
import com.sling.hotel.domain.port.out.SearchEventPublisher;
import com.sling.hotel.domain.port.out.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public SearchService searchService(SearchEventPublisher eventPublisher,
                                       SearchRepository repository) {
        return new SearchService(eventPublisher, repository);
    }
}

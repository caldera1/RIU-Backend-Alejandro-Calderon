package com.sling.hotel.infrastructure.adapter.in.rest;

import com.sling.hotel.infrastructure.adapter.out.persistence.SpringDataSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class SearchControllerIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.1");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataSearchRepository searchRepository;

    @BeforeEach
    void setUp() {
        searchRepository.deleteAll();
    }

    @Test
    void fullFlowPostSearchThenGetCount() throws Exception {
        var body = """
                {"hotelId":"1234aBc","checkIn":"29/12/2023","checkOut":"31/12/2023","ages":[30,29,1,3]}
                """;

        var result = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").isNotEmpty())
                .andReturn();

        var searchId = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.searchId").toString();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                mockMvc.perform(get("/count").param("searchId", searchId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.searchId").value(searchId))
                        .andExpect(jsonPath("$.search.hotelId").value("1234aBc"))
                        .andExpect(jsonPath("$.search.checkIn").value("29/12/2023"))
                        .andExpect(jsonPath("$.search.checkOut").value("31/12/2023"))
                        .andExpect(jsonPath("$.search.ages[0]").value(30))
                        .andExpect(jsonPath("$.search.ages[1]").value(29))
                        .andExpect(jsonPath("$.search.ages[2]").value(1))
                        .andExpect(jsonPath("$.search.ages[3]").value(3))
                        .andExpect(jsonPath("$.count").value(1))
        );
    }

    @Test
    void identicalSearchesShouldIncrementCount() throws Exception {
        var body = """
                {"hotelId":"H1","checkIn":"01/01/2024","checkOut":"05/01/2024","ages":[25]}
                """;

        var searchId1 = postSearchAndGetId(body);
        postSearchAndGetId(body);
        postSearchAndGetId(body);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(searchRepository.count()).isEqualTo(3);
        });

        mockMvc.perform(get("/count").param("searchId", searchId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void differentAgesOrderShouldNotShareCount() throws Exception {
        var body1 = """
                {"hotelId":"H1","checkIn":"01/01/2024","checkOut":"05/01/2024","ages":[30,29]}
                """;
        var body2 = """
                {"hotelId":"H1","checkIn":"01/01/2024","checkOut":"05/01/2024","ages":[29,30]}
                """;

        var searchId1 = postSearchAndGetId(body1);
        var searchId2 = postSearchAndGetId(body2);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(searchRepository.count()).isEqualTo(2);
        });

        mockMvc.perform(get("/count").param("searchId", searchId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        mockMvc.perform(get("/count").param("searchId", searchId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getCountShouldReturn404ForUnknownSearchId() throws Exception {
        mockMvc.perform(get("/count").param("searchId", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    private String postSearchAndGetId(String body) throws Exception {
        var result = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.searchId").toString();
    }
}

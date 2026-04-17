package com.sling.hotel.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sling.hotel.application.service.SearchNotFoundException;
import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.model.SearchCount;
import com.sling.hotel.application.port.in.CountUseCase;
import com.sling.hotel.application.port.in.SearchUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SearchUseCase searchUseCase;

    @MockitoBean
    private CountUseCase countUseCase;

    @Test
    void postSearchShouldReturnSearchId() throws Exception {
        when(searchUseCase.search(any())).thenReturn("abc-123");

        var body = new SearchRequest("H1", "29/12/2023", "31/12/2023", List.of(30, 29));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("abc-123"));
    }

    @Test
    void postSearchShouldReturn400WhenHotelIdIsBlank() throws Exception {
        var body = new SearchRequest("", "29/12/2023", "31/12/2023", List.of(30));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchShouldReturn400WhenAgesIsEmpty() throws Exception {
        var body = new SearchRequest("H1", "29/12/2023", "31/12/2023", List.of());

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchShouldReturn400WhenCheckInAfterCheckOut() throws Exception {
        var body = new SearchRequest("H1", "31/12/2023", "29/12/2023", List.of(30));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchShouldReturn400WhenCheckInEqualsCheckOut() throws Exception {
        var body = new SearchRequest("H1", "29/12/2023", "29/12/2023", List.of(30));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchShouldReturn400WhenDateFormatIsInvalid() throws Exception {
        var body = new SearchRequest("H1", "2023-12-29", "2023-12-31", List.of(30));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearchShouldReturn400WhenFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCountShouldReturnSearchCount() throws Exception {
        var search = new HotelSearch("H1", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29));
        when(countUseCase.count("abc-123")).thenReturn(new SearchCount("abc-123", search, 5));

        mockMvc.perform(get("/count").param("searchId", "abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("abc-123"))
                .andExpect(jsonPath("$.search.hotelId").value("H1"))
                .andExpect(jsonPath("$.search.checkIn").value("29/12/2023"))
                .andExpect(jsonPath("$.search.checkOut").value("31/12/2023"))
                .andExpect(jsonPath("$.search.ages[0]").value(30))
                .andExpect(jsonPath("$.search.ages[1]").value(29))
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void getCountShouldReturn404WhenNotFound() throws Exception {
        when(countUseCase.count("unknown")).thenThrow(new SearchNotFoundException("unknown"));

        mockMvc.perform(get("/count").param("searchId", "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postSearchShouldReturn400WhenAgesContainsNegativeValue() throws Exception {
        var body = new SearchRequest("H1", "29/12/2023", "31/12/2023", List.of(-1, 30));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}

package com.sling.hotel.infrastructure.adapter.in.rest;

import com.sling.hotel.domain.model.HotelSearch;
import com.sling.hotel.domain.model.SearchCount;
import com.sling.hotel.domain.port.in.CountUseCase;
import com.sling.hotel.domain.port.in.SearchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@Tag(name = "Hotel Availability", description = "Hotel availability search endpoints")
public class SearchController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SearchUseCase searchUseCase;
    private final CountUseCase countUseCase;

    public SearchController(SearchUseCase searchUseCase, CountUseCase countUseCase) {
        this.searchUseCase = searchUseCase;
        this.countUseCase = countUseCase;
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Submit a hotel availability search",
            description = "Publishes the search to Kafka and returns a unique search ID")
    @ApiResponse(responseCode = "200", description = "Search submitted",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public SearchResponse search(@Valid @RequestBody SearchRequest request) {
        var checkIn = LocalDate.parse(request.checkIn(), DATE_FORMAT);
        var checkOut = LocalDate.parse(request.checkOut(), DATE_FORMAT);

        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidSearchException("checkIn must be before checkOut");
        }

        var hotelSearch = new HotelSearch(request.hotelId(), checkIn, checkOut, request.ages());
        var searchId = searchUseCase.search(hotelSearch);
        return new SearchResponse(searchId);
    }

    @GetMapping("/count")
    @Operation(summary = "Get search count",
            description = "Returns how many times an identical search has been performed")
    @ApiResponse(responseCode = "200", description = "Search count retrieved",
            content = @Content(schema = @Schema(implementation = CountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Search ID not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public CountResponse count(@RequestParam String searchId) {
        var result = countUseCase.count(searchId);
        return toCountResponse(result);
    }

    private CountResponse toCountResponse(SearchCount searchCount) {
        var search = searchCount.search();
        var detail = new CountResponse.SearchDetail(
                search.hotelId(),
                search.checkIn().format(DATE_FORMAT),
                search.checkOut().format(DATE_FORMAT),
                search.ages());
        return new CountResponse(searchCount.searchId(), detail, searchCount.count());
    }
}

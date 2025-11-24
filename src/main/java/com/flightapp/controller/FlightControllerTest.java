package com.flightapp.controller;

package com.flightapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.flightapp.dto.InventoryRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
@AutoConfigureWebTestClient
class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addInventory_shouldReturnCreated() {

        InventoryRequest request = new InventoryRequest();
        request.setAirlineName("Test Airline");
        request.setAirlineLogoUrl("https://example.com/logo.png");
        request.setFromPlace("Kolkata");
        request.setToPlace("Delhi");
        request.setDepartureDate(LocalDate.now().plusDays(5));
        request.setDepartureTime(LocalTime.of(10, 30));
        request.setArrivalTime(LocalTime.of(12, 30));
        request.setOneWayPrice(BigDecimal.valueOf(5000));
        request.setRoundTripPrice(BigDecimal.valueOf(9000));
        request.setRoundTripAvailable(true);
        request.setTotalSeats(100);

        webTestClient.post()
                .uri("/api/v1.0/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.airlineName").isEqualTo("Test Airline");
    }
}
package com.flightapp.controller;
import com.flightapp.dto.UpdateBookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.TicketResponse;
import com.flightapp.exception.BusinessException;
import com.flightapp.model.Flight;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@Validated
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    // POST /api/v1.0/flight/airline/inventory
    @PostMapping(
            path = "/airline/inventory",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Flight>> addInventory(@Valid @RequestBody InventoryRequest request) {
        return flightService.addInventory(request)
                .map(savedFlight -> ResponseEntity.status(HttpStatus.CREATED).body(savedFlight));
    }

    // POST /api/v1.0/flight/search
    @PostMapping(
            path = "/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_NDJSON_VALUE
    )
    public Flux<FlightSearchResponse> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }
    
 // POST /api/v1.0/flight/search/ids
    @PostMapping(
            path = "/search/ids",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<String> searchFlightIds(@Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlightIds(request);
    }

    
    

    // POST /api/v1.0/flight/booking/{flightId}
    @PostMapping(
            path = "/booking/{flightId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<TicketResponse>> bookTicket(
            @PathVariable("flightId") String flightId,
            @RequestHeader("X-User-Email") String loggedInEmail,
            @Valid @RequestBody BookingRequest request) {

        if (!loggedInEmail.equalsIgnoreCase(request.getUserEmail())) {
            return Mono.error(new BusinessException(
                    "Logged-in user email does not match booking email"));
        }

        return bookingService.bookTicket(flightId, request)
                .map(ticket -> ResponseEntity.status(HttpStatus.CREATED).body(ticket));
    }

    // GET /api/v1.0/flight/ticket/{pnr}
    @GetMapping(
            path = "/ticket/{pnr}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<TicketResponse>> getTicket(@PathVariable("pnr") String pnr) {
        return bookingService.getTicketByPnr(pnr)
                .map(ResponseEntity::ok);
    }

    // GET /api/v1.0/flight/booking/history/{emailId}
    @GetMapping(
            path = "/booking/history/{emailId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<TicketResponse> getBookingHistory(
            @PathVariable("emailId") String emailId,
            @RequestHeader("X-User-Email") String loggedInEmail) {

        if (!loggedInEmail.equalsIgnoreCase(emailId)) {
            return Flux.error(new BusinessException(
                    "You can only view booking history for your own email"));
        }

        return bookingService.getBookingHistory(emailId);
    }

    // DELETE /api/v1.0/flight/booking/cancel/{pnr}
    @DeleteMapping(path = "/booking/cancel/{pnr}")
    public Mono<ResponseEntity<Void>> cancelBooking(
            @PathVariable("pnr") String pnr,
            @RequestHeader("X-User-Email") String loggedInEmail) {

        return bookingService.cancelBooking(pnr, loggedInEmail)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
    
    @PutMapping(
            path = "/booking/update/{pnr}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<TicketResponse>> updateBookingMeal(
            @PathVariable("pnr") String pnr,
            @RequestHeader("X-User-Email") String loggedInEmail,
            @Valid @RequestBody UpdateBookingRequest request) {

        return bookingService.updateMealType(pnr, loggedInEmail, request.getMealType())
                .map(ResponseEntity::ok);
    }

}

package com.flightapp.service;

import org.springframework.stereotype.Service;

import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Mono<Flight> addInventory(InventoryRequest request) {

        Flight flight = new Flight();
        flight.setAirlineName(request.getAirlineName());
        flight.setAirlineLogoUrl(request.getAirlineLogoUrl());
        flight.setFromPlace(request.getFromPlace());
        flight.setToPlace(request.getToPlace());
        flight.setDepartureDate(request.getDepartureDate());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setOneWayPrice(request.getOneWayPrice());
        flight.setRoundTripPrice(request.getRoundTripPrice());
        flight.setRoundTripAvailable(request.isRoundTripAvailable());
        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(request.getTotalSeats());

        return flightRepository.save(flight);
    }

    public Flux<FlightSearchResponse> searchFlights(FlightSearchRequest request) {

        return flightRepository
                .findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureDate(
                        request.getFromPlace(),
                        request.getToPlace(),
                        request.getTravelDate()
                )
                .map(this::mapToSearchResponse);
    }

    private FlightSearchResponse mapToSearchResponse(Flight flight) {
        FlightSearchResponse response = new FlightSearchResponse();
        response.setFlightId(flight.getId());
        response.setAirlineName(flight.getAirlineName());
        response.setAirlineLogoUrl(flight.getAirlineLogoUrl());
        response.setFromPlace(flight.getFromPlace());
        response.setToPlace(flight.getToPlace());
        response.setDepartureDate(flight.getDepartureDate());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setOneWayPrice(flight.getOneWayPrice());
        response.setRoundTripPrice(flight.getRoundTripPrice());
        response.setRoundTripAvailable(flight.isRoundTripAvailable());
        response.setAvailableSeats(flight.getAvailableSeats());
        return response;
    }
}

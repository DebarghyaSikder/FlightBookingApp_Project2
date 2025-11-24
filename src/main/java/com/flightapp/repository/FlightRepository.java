package com.flightapp.repository;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.model.Flight;

import reactor.core.publisher.Flux;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {

    Flux<Flight> findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureDate(
            String fromPlace,
            String toPlace,
            LocalDate departureDate
    );
}

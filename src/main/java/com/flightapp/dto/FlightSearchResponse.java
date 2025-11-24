package com.flightapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightSearchResponse {

    private String flightId;

    private String airlineName;
    private String airlineLogoUrl;

    private String fromPlace;
    private String toPlace;

    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    private BigDecimal oneWayPrice;
    private BigDecimal roundTripPrice;

    private boolean roundTripAvailable;
    private int availableSeats;

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAirlineLogoUrl() {
        return airlineLogoUrl;
    }

    public void setAirlineLogoUrl(String airlineLogoUrl) {
        this.airlineLogoUrl = airlineLogoUrl;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getOneWayPrice() {
        return oneWayPrice;
    }

    public void setOneWayPrice(BigDecimal oneWayPrice) {
        this.oneWayPrice = oneWayPrice;
    }

    public BigDecimal getRoundTripPrice() {
        return roundTripPrice;
    }

    public void setRoundTripPrice(BigDecimal roundTripPrice) {
        this.roundTripPrice = roundTripPrice;
    }

    public boolean isRoundTripAvailable() {
        return roundTripAvailable;
    }

    public void setRoundTripAvailable(boolean roundTripAvailable) {
        this.roundTripAvailable = roundTripAvailable;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}

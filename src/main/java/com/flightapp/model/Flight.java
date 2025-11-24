package com.flightapp.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    private String airlineName;
    private String airlineLogoUrl;

    private String fromPlace;
    private String toPlace;

    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    private boolean roundTripAvailable;

    private BigDecimal oneWayPrice;
    private BigDecimal roundTripPrice;

    private int totalSeats;
    private int availableSeats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isRoundTripAvailable() {
        return roundTripAvailable;
    }

    public void setRoundTripAvailable(boolean roundTripAvailable) {
        this.roundTripAvailable = roundTripAvailable;
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

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}

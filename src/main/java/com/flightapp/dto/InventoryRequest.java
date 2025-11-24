package com.flightapp.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventoryRequest {

    @NotBlank(message = "Airline name is required")
    private String airlineName;

    private String airlineLogoUrl;

    @NotBlank(message = "From place is required")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    private String toPlace;

    @NotNull(message = "Departure date is required")
    @FutureOrPresent(message = "Departure date cannot be in the past")
    private LocalDate departureDate;

    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;

    @NotNull(message = "One way price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal oneWayPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Round trip price must be positive")
    private BigDecimal roundTripPrice;

    private boolean roundTripAvailable;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "At least one seat is required")
    private Integer totalSeats;

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

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
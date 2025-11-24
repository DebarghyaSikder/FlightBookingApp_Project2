package com.flightapp.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.TicketResponse;
import com.flightapp.exception.BusinessException;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.model.enums.MealType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PnrGenerator pnrGenerator;

    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          PnrGenerator pnrGenerator) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.pnrGenerator = pnrGenerator;
    }

    public Mono<TicketResponse> bookTicket(String flightId, BookingRequest request) {

    	
    	 if (request.getPassengers() == null || request.getSeatNumbers() == null) {
    	        return Mono.error(new BusinessException("Passengers and seat numbers are required"));
    	    }

    	    int requested = request.getNumberOfSeats();
    	    int passengerCount = request.getPassengers().size();
    	    int seatCount = request.getSeatNumbers().size();

    	    if (requested != passengerCount || requested != seatCount) {
    	        return Mono.error(new BusinessException(
    	                "Number of seats must match passengers count and seat numbers count"));
    	    }

    	    
        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Flight not found with id: " + flightId)))
                .flatMap(flight -> {

                    if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
                        return Mono.error(new BusinessException("Not enough seats available"));
                    }

                    int updatedSeats = flight.getAvailableSeats() - request.getNumberOfSeats();
                    flight.setAvailableSeats(updatedSeats);

                    return flightRepository.save(flight)
                            .flatMap(savedFlight -> {

                                Booking booking = new Booking();
                                booking.setPnr(pnrGenerator.generatePnr());
                                booking.setFlightId(savedFlight.getId());
                                booking.setUserName(request.getUserName());
                                booking.setUserEmail(request.getUserEmail());
                                booking.setNumberOfSeats(request.getNumberOfSeats());
                                booking.setPassengers(request.getPassengers());
                                booking.setSeatNumbers(request.getSeatNumbers());
                                booking.setMealType(request.getMealType());
                                booking.setStatus(BookingStatus.BOOKED);
                                booking.setBookedAt(LocalDateTime.now());
                                booking.setJourneyDate(savedFlight.getDepartureDate());
                                booking.setJourneyDepartureDateTime(
                                        LocalDateTime.of(
                                                savedFlight.getDepartureDate(),
                                                savedFlight.getDepartureTime()
                                        )
                                );

                                return bookingRepository.save(booking)
                                        .map(b -> mapToTicketResponse(b, savedFlight));
                            });
                });
    }

    public Mono<TicketResponse> getTicketByPnr(String pnr) {

        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Booking not found for PNR: " + pnr)))
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .switchIfEmpty(Mono.error(
                                        new ResourceNotFoundException("Flight not found")))
                                .map(flight -> mapToTicketResponse(booking, flight))
                );
    }

    public Flux<TicketResponse> getBookingHistory(String emailId) {

        return bookingRepository.findByUserEmailOrderByBookedAtDesc(emailId)
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .map(flight -> mapToTicketResponse(booking, flight))
                );
    }

    public Mono<Void> cancelBooking(String pnr, String loggedInEmail) {

        LocalDateTime now = LocalDateTime.now();

        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Booking not found for PNR: " + pnr)))
                .flatMap(booking -> {

                    if (!booking.getUserEmail().equalsIgnoreCase(loggedInEmail)) {
                        return Mono.error(new BusinessException(
                                "You can cancel only your own bookings"));
                    }

                    if (booking.getStatus() == BookingStatus.CANCELLED) {
                        return Mono.error(new BusinessException("Ticket already cancelled"));
                    }

                    LocalDateTime journeyTime = booking.getJourneyDepartureDateTime();
                    if (journeyTime.minusHours(24).isBefore(now)) {
                        return Mono.error(new BusinessException(
                                "Ticket can only be cancelled more than 24 hours before journey time"));
                    }

                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setCancelledAt(LocalDateTime.now());

                    return flightRepository.findById(booking.getFlightId())
                            .switchIfEmpty(Mono.error(
                                    new ResourceNotFoundException("Flight not found")))
                            .flatMap(flight -> {
                                int freedSeats = booking.getNumberOfSeats();
                                flight.setAvailableSeats(flight.getAvailableSeats() + freedSeats);
                                return flightRepository.save(flight)
                                        .then(bookingRepository.save(booking))
                                        .then();
                            });
                });
    }
    
    
    
    public Mono<TicketResponse> updateMealType(String pnr,
            String loggedInEmail,
            MealType newMealType) {

LocalDateTime now = LocalDateTime.now();

return bookingRepository.findByPnr(pnr)
.switchIfEmpty(Mono.error(
new ResourceNotFoundException("Booking not found for PNR: " + pnr)))
.flatMap(booking -> {

if (!booking.getUserEmail().equalsIgnoreCase(loggedInEmail)) {
return Mono.error(new BusinessException(
"You can update only your own bookings"));
}

if (booking.getStatus() == BookingStatus.CANCELLED) {
return Mono.error(new BusinessException(
"Cannot update a cancelled booking"));
}

LocalDateTime journeyTime = booking.getJourneyDepartureDateTime();
if (journeyTime.minusHours(24).isBefore(now)) {
return Mono.error(new BusinessException(
"Ticket can only be updated more than 24 hours before journey time"));
}

booking.setMealType(newMealType);

return flightRepository.findById(booking.getFlightId())
.switchIfEmpty(Mono.error(
 new ResourceNotFoundException("Flight not found")))
.flatMap(flight ->
 bookingRepository.save(booking)
         .map(saved -> mapToTicketResponse(saved, flight))
);
});
}



    private TicketResponse mapToTicketResponse(Booking booking, Flight flight) {

        TicketResponse response = new TicketResponse();
        response.setPnr(booking.getPnr());
        response.setFlightId(flight.getId());
        response.setAirlineName(flight.getAirlineName());
        response.setAirlineLogoUrl(flight.getAirlineLogoUrl());
        response.setFromPlace(flight.getFromPlace());
        response.setToPlace(flight.getToPlace());
        response.setDepartureDate(flight.getDepartureDate());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setUserName(booking.getUserName());
        response.setUserEmail(booking.getUserEmail());
        response.setNumberOfSeats(booking.getNumberOfSeats());
        response.setPassengers(booking.getPassengers());
        response.setSeatNumbers(booking.getSeatNumbers());
        response.setMealType(booking.getMealType());
        response.setStatus(booking.getStatus());
        response.setBookedAt(booking.getBookedAt());
        response.setCancelledAt(booking.getCancelledAt());
        return response;
    }
}

# Flight Booking Service – WebFlux + MongoDB

This project is a backend service for a simple flight booking system.  
It started as a blocking Spring MVC + MySQL app and was then rebuilt using **Spring WebFlux** and **MongoDB** with a fully reactive stack.

The idea is to provide a clean, layered API that supports:

- Searching flights
- Managing flight inventory
- Booking tickets for a logged-in user
- Viewing ticket details and booking history
- Updating and cancelling bookings with business rules (24-hour cancellation window, etc.)

---

## Tech Stack

- **Language**: Java (JDK 18)
- **Framework**: Spring Boot 3 (WebFlux)
- **Database**: MongoDB (Reactive)
- **Build Tool**: Maven
- **Validation**: Jakarta Bean Validation (`jakarta.validation`)
- **Logging**: Spring Boot default logging
- **Testing**:
  - JUnit 5
  - Spring Boot Test
  - WebTestClient (for WebFlux)
  - Reactor Test (`StepVerifier`)
- **Code Quality (optional)**: SonarCloud

---

## Architecture & Packages

The project follows a simple MVC + layered architecture:

- `com.flightapp`
  - `controller` – REST controllers (expose endpoints)
  - `service` – business logic (booking rules, validations beyond annotations)
  - `repository` – reactive Mongo repositories
  - `model` – domain models for MongoDB documents
  - `model.enums` – enums like `MealType`, `BookingStatus`, `Gender`
  - `dto` – request/response DTOs for clean API contracts
  - `exception` – custom exceptions + global exception handler
  - `FlightWebfluxMongoApplication` – Spring Boot main class

Mongo collections:

- `flights`
- `bookings`

---

## Features

### Flight Inventory

- Add new flights with:
  - Airline name and logo URL
  - From/To place
  - Departure & arrival date/time
  - One-way and round-trip prices
  - Total seats & available seats
  - Whether round-trip is supported

Business rules:
- `fromPlace` and `toPlace` cannot be the same.
- Departure date cannot be in the past.
- Arrival time cannot be before departure time (for same-day flights).
- If `roundTripAvailable = true`, round-trip price must be provided.
- Total seats must be at least 1.

---

### Flight Search

Search flights based on:

- From place
- To place
- Travel date
- Trip type: `ONE_WAY` or `ROUND_TRIP`

Two flavours:

1. **Full search** – returns full flight details.  
2. **ID only search** – returns only matching flight IDs (useful just to check presence).

If no flights match, the ID-only search returns **404 Not Found** with a clear message.

---

### Booking & Ticketing

Users can:

- Book one or more seats on a flight.
- Provide passenger details:
  - Name
  - Gender
  - Age
- Select seat numbers and meal type (`VEG` / `NON_VEG`).
- Get a generated PNR for each booking.

Business rules:

- Booking is *simulated* as “logged-in only” by requiring a header:  
  `X-User-Email: <user email>`  
  and this must match the email in the request body.
- `numberOfSeats` must match:
  - number of passengers
  - number of seat numbers
- Seat numbers must be non-blank and unique.
- Seats cannot be booked if `availableSeats` is too low.
- A booking stores both:
  - High-level info (PNR, user email, status)
  - Snapshot of flight info (date, times) at booking time.

---

### Ticket Viewing, History, Update & Cancellation

- **View ticket by PNR** – returns full ticket info.
- **View booking history by email** – lists all bookings for that user (most recent first).
- **Update ticket** – allows changing only the `mealType` before the 24-hour window closes.
- **Cancel ticket** – frees up seats and changes status to `CANCELLED`.

Business rules:

- Only the owner (same `userEmail` as header) can view history, update, or cancel.
- Tickets can be **updated/cancelled only if**:
  - Status is not already `CANCELLED`
  - Journey time is more than 24 hours away.
- Second attempt to cancel an already cancelled booking returns **404 Not Found**.

---

## API Endpoints
1. Add Flight Inventory

- POST /api/v1.0/flight/airline/inventory

-This is the endpoint used internally by the “airline admin” to add a new flight to the system. It stores details like the airline name, route, timings, prices, and number of seats. If a flight is added successfully, the service returns 201 Created along with the generated flight ID.

2. Search Flights (Full Details)

- POST /api/v1.0/flight/search

- This endpoint is used by users to search flights between two places on a given date. It returns full details of matching flights — timings, prices, airline, and available seats.
- It always returns a 200 OK for a valid search

3. Search Flights (IDs Only)

- POST /api/v1.0/flight/search/ids

- This is a lighter version of the search API. Instead of returning full flight details, it only sends back the IDs of matching flights.
- If no flights match, this one returns 404 Not Found with a clear message.


 4. Book Ticket

- POST /api/v1.0/flight/booking/{flightId}
- Header required: X-User-Email

- This is the main booking endpoint. The user provides their own email, passenger list, seat numbers, and meal preference.
- The server verifies a few things:
- The email in header must match the email in the body
- Seat numbers must be unique and non-empty
- Number of passengers must match number of seats
- The flight must have enough available seats

-If everything checks out, the booking is confirmed and a PNR is generated.
-Response code is 201 Created.

5. Get Ticket by PNR

- GET /api/v1.0/flight/ticket/{pnr}

- This endpoint fetches the complete ticket details for a given PNR.
- If the PNR is valid, you get the flight info, booking status, passenger list, etc.
- If not, the endpoint returns 404 Not Found.
- This is typically used right after booking, or when a user wants to view their ticket later.

6. View Booking History

- GET /api/v1.0/flight/booking/history/{emailId}
- Header required: X-User-Email

- This fetches all bookings made by a particular user.
- The email in the header must match the email in the path — this prevents users from viewing each other’s booking history.
- If the user has no bookings, it simply returns an empty list with 200 OK.

 7. Update Ticket (Meal Type Only)

- PUT /api/v1.0/flight/booking/update/{pnr}
- Header required: X-User-Email

- This endpoint allows the user to change their meal preference (VEG to NON_VEG, or vice versa).
- Only the owner of the ticket can update it.

Some rules for this :
- The ticket must not be cancelled
- The journey must be more than 24 hours away
- Meal type is the only thing that can be updated
- If all conditions are met, the ticket is updated and 200 OK is returned.

 8. Cancel Ticket

- DELETE /api/v1.0/flight/booking/cancel/{pnr}
- Header required: X-User-Email

- This endpoint cancels a ticket and releases the booked seats back to the flight.
- Cancellation is only allowed if the journey is more than 24 hours away.
- The first cancellation attempt returns 204 No Content.

- But if the same PNR is cancelled again:
- It returns 404 Not Found (because the ticket is already cancelled)
- This prevents duplicate cancellations.
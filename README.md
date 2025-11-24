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



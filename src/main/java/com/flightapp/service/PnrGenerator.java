package com.flightapp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class PnrGenerator {

    public String generatePnr() {
        String uuidPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        String timePart = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("ddHHmm"));

        return uuidPart + timePart;
    }
}

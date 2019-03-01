package io.codelex.flightplanner.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;


public class FindTripRequest {
    private final Airport from;
    private final Airport to;
    private final String carrier;
    private final LocalDate departure;
    private final LocalDate arrival;


    @JsonCreator
    public FindTripRequest(@JsonProperty("from") Airport from,
                           @JsonProperty("to") Airport to,
                           @JsonProperty("carrier") String carrier,
                           @JsonProperty("departureTime") LocalDate departure,
                           @JsonProperty("arrivalTime") LocalDate arrival) {
        this.from = from;
        this.to = to;
        this.carrier = carrier;
        this.departure = departure;
        this.arrival = arrival;

    }

    public Airport getFrom() {
        return from;
    }

    public Airport getTo() {
        return to;
    }

    public String getCarrier() {
        return carrier;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public LocalDate getArrival() {
        return arrival;
    }
}

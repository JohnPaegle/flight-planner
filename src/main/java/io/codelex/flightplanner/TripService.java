package io.codelex.flightplanner;

import io.codelex.flightplanner.api.AddTripsRequest;

import io.codelex.flightplanner.api.Airport;

import io.codelex.flightplanner.api.FindTripRequest;
import io.codelex.flightplanner.api.Trip;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Component
class TripService {
    private final AtomicLong ID = new AtomicLong();
    private final List<Trip> trips = new ArrayList<>();

    synchronized Trip addTrip(AddTripsRequest request) {
        if (isTripPresent(request)) {
            throw new IllegalStateException();
        } else {
            Trip trip = new Trip(
                    ID.incrementAndGet(),
                    request.getFrom(),
                    request.getTo(),
                    request.getCarrier(),
                    request.getDepartureTime(),
                    request.getArrivalTime()
            );
            trips.add(trip);
            return trip;
        }
    }

    synchronized List<Trip> findAll() {
        return trips;
    }

    synchronized List<Trip> findByJSON(FindTripRequest request) {
        return trips.stream()
                .filter(it -> it.getFrom().equals(request.getFrom()))
                .filter(it -> it.getTo().equals(request.getTo()))
                .filter(it -> it.getDepartureTime().toLocalDate().isEqual(request.getDeparture()))
                .filter(it -> it.getArrivalTime().toLocalDate().isEqual(request.getArrival()))
                .collect(Collectors.toList());
    }

    synchronized Trip findById(Long id) throws NoSuchElementException {
        for (Trip trip : trips) {
            if (trip.getId().equals(id)) {
                return trip;
            }
        }
        throw new NoSuchElementException();
    }

    synchronized List<Trip> search(String from, String to) {
        List<Trip> responseList = new ArrayList<>();
        for (Trip trip : trips) {
            if (isAirportMatching(trip.getFrom(), from)) {
                responseList.add(trip);
                break;
            }
            if (isAirportMatching(trip.getTo(), to)) {
                responseList.add(trip);
                break;
            }
        }
        return responseList;
    }

    synchronized void deleteById(Long id) {
        for (Trip trip : trips) {
            if (trip.getId().equals(id)) {
                trips.remove(trip);
                return;
            }
        }
    }

    void deleteAll() {
        trips.clear();
    }

    synchronized boolean isTripPresent(AddTripsRequest request) {
        for (Trip trip : trips) {
            if (trip.getFrom().equals(request.getFrom())
                    && trip.getTo().equals(request.getTo())
                    && trip.getCarrier().equals(request.getCarrier())
                    && trip.getDepartureTime().equals(request.getDepartureTime())
                    && trip.getArrivalTime().equals(request.getArrivalTime())) {
                return true;
            }
        }
        return false;
    }

    synchronized boolean isAirportMatching(Airport airport, String search) {
        if (search != null && !search.isEmpty() && !search.equals(" ")) {
            return airport.getCity().toLowerCase().trim().contains(search.toLowerCase().trim())
                    || airport.getCountry().toLowerCase().trim().contains(search.toLowerCase().trim())
                    || airport.getAirport().toLowerCase().trim().contains(search.toLowerCase().trim());
        } else return false;
    }
}










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



@Component
class TripService {

    private final List<Trip> trips = new ArrayList<>();
    private final AtomicLong id = new AtomicLong();

    Trip addTrip(AddTripsRequest request) {
        if (isTripPresent(request)) {
            throw new IllegalStateException();
        } else {
            Trip trip = new Trip(
                    id.incrementAndGet(),
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
    

    List<Trip> search(String from, String to) {
        List<Trip> responseList = new ArrayList<>();
        for (Trip trip : trips) {
            if (doesAirportMatch(trip.getFrom(), from)) {
                responseList.add(trip);
                break;
            }
            if (doesAirportMatch(trip.getTo(), to)) {
                responseList.add(trip);
                break;
            }
        }
        return responseList;
    }

    List<Trip> findAll() {
        return trips;
    }

    Trip findById(long id) throws NoSuchElementException  {
        for (Trip trip : trips) {
            if (trip.getId().equals(id)) {
                return trip;
            }
        }
        throw new NoSuchElementException();
    }

    void deleteById(Long id) {
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
    
    private Boolean requestIsEmty(Airport airport) {
        if (airport.getAirport().length() == 0
                || airport.getCity().length() == 0
                || airport.getCountry().length() == 0) {
            return true;
        } else return false;
    }

    public boolean doesAirportMatch(Airport airport, String search) {
        if (search != null && search.length() > 0) {
            return airport.getCity().toLowerCase().trim().contains(search.toLowerCase().trim())
                    || search.toLowerCase().trim().contains(airport.getCity().toLowerCase().trim())
                    || airport.getCountry().trim().toLowerCase().contains(search.toLowerCase().trim())
                    || search.toLowerCase().trim().contains(airport.getCountry().trim())
                    || airport.getAirport().trim().toLowerCase().contains(search.toLowerCase().trim())
                    || search.toLowerCase().trim().contains(airport.getAirport().toLowerCase().trim());
        }
        return false;

    }

    public boolean isTripPresent(AddTripsRequest request) {
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
   

    List<Trip> findByJSON(FindTripRequest request) {
        List<Trip> x = new ArrayList<>();
        for (Trip trip : trips) {
            if (trip.getFrom().equals(request.getFrom())
                    && trip.getTo().equals(request.getTo())
                    && trip.getCarrier().equals(request.getCarrier())
                    && trip.getDepartureTime().equals(request.getDepartureTime())
                    && trip.getArrivalTime().equals(request.getArrivalTime())) {
                x.add(trip);
            }
        }
        return x;
    }
    
}
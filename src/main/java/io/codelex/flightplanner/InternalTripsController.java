package io.codelex.flightplanner;


import io.codelex.flightplanner.api.AddTripsRequest;
import io.codelex.flightplanner.api.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.NoSuchElementException;

@RestController
@RequestMapping("/internal-api")
class InternalTripsController {
    @Autowired
    private TripService tripService;

    @PutMapping("/flights")
    public ResponseEntity<Trip> addTrip(@RequestBody AddTripsRequest request) {
        if (isInputCorrect(request)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (tripService.isTripPresent(request)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else
            return new ResponseEntity<>(tripService.addTrip(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/flights/{id}")
    public ResponseEntity<Void> deleteTripById(@PathVariable Long id) {
        try {
            tripService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/flights/{id}")
    public ResponseEntity<Trip> findTripById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(tripService.findById(id), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clear")
    public void deleteAll() {
        tripService.deleteAll();
    }

    private boolean isInputCorrect(AddTripsRequest request) {
        if (request.getFrom() == null) {
            return false;
        } else if (request.getTo() == null) {
            return false;
        } else if (request.getDepartureTime() == null) {
            return false;
        } else if (request.getArrivalTime() == null) {
            return false;
        } else if (request.getCarrier() == null) {
            return false;
        } else if (request.getCarrier().trim().equals("")) {
            return false;
        } else if (request.getFrom().getCountry() == null) {
            return false;
        } else if (request.getFrom().getCity() == null) {
            return false;
        } else if (request.getFrom().getAirport() == null) {
            return false;
        } else if (request.getTo().getCountry() == null) {
            return false;
        } else if (request.getTo().getCity() == null) {
            return false;
        } else if (request.getTo().getAirport() == null) {
            return false;
        } else if (request.getFrom().equals(request.getTo())) {
            return false;
        } else if (request.getTo().getCountry().trim().equals("")) {
            return false;
        } else if (request.getTo().getCity().trim().equals("")) {
            return false;
        } else if (request.getTo().getAirport().trim().equals("")) {
            return false;
        } else if (request.getFrom().getCountry().trim().equals("")) {
            return false;
        } else if (request.getFrom().getCity().trim().equals("")) {
            return false;
        } else if (request.getFrom().getAirport().trim().equals("")) {
            return false;
        } else if (request.getFrom().getAirport().toLowerCase().trim().equals(request.getTo().getAirport().toLowerCase().trim())) {
            return false;
        } else if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            return false;
        } else if (request.getArrivalTime().isEqual(request.getDepartureTime())) {
            return false;
        } else return true;
    }
}


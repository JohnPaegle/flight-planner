package io.codelex.flightplanner;


import io.codelex.flightplanner.api.FindTripRequest;
import io.codelex.flightplanner.api.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
 class PublicTripsController {

    @Autowired
    private TripService tripService;

    @GetMapping("/flights/search")
    public ResponseEntity<List<Trip>> search(String from, String to) {
            if (from == null && to == null) {
                return new ResponseEntity<>(tripService.findAll(), HttpStatus.OK);
            }
            return new ResponseEntity<>(tripService.search(from, to), HttpStatus.OK);
        }

    @PostMapping("/flights")
    public ResponseEntity<List<Trip>> findTrip(@RequestBody FindTripRequest request) {
        if (isInputCorrect(request)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(tripService.findByJSON(request), HttpStatus.OK);
        }
    }

    @GetMapping("/flights/{id}")
    public ResponseEntity<Trip> findTripById(@PathVariable long id) {
        Trip trip = tripService.findById(id);
        if (trip != null) {
            return new ResponseEntity<>(tripService.findById(id), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    private boolean isInputCorrect(FindTripRequest request) {
        if (request.getTo() == null) {
            return false;
        } else if (request.getFrom() == null) {
            return false;
        } else if (request.getArrivalTime() == null) {
            return false;
        } else if (request.getDepartureTime() == null) {
            return false;
        } else if (request.getCarrier() == null) {
            return false;
        } else if (request.getTo().getCity().equals("")) {
            return false;
        } else if (request.getTo().getAirport().equals("")) {
            return false;
        } else if (request.getTo().getCountry().equals("")) {
            return false;
        } else if (request.getFrom().getCountry().equals("")) {
            return false;
        } else if (request.getFrom().getAirport().equals("")) {
            return false;
        } else if (request.getFrom().getCity().equals("")) {
            return false;
        } else if (request.getCarrier().equals("")) {
            return false;
        }
        return true;
    }

}
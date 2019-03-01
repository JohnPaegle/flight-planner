package io.codelex.flightplanner;


import io.codelex.flightplanner.api.FindTripRequest;
import io.codelex.flightplanner.api.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
class PublicTripsController {

    @Autowired
    private TripService tripService;

    @GetMapping("/flights/search")
    public ResponseEntity<List<Trip>> search(@Valid @NotNull String from, String to) {
        if (from.equals(to)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tripService.search(from, to), HttpStatus.OK);
    }

    @PostMapping("/flights")
    public ResponseEntity<List<Trip>> findFlight(@Valid @NotNull @RequestBody FindTripRequest request) {
        if (request.getTo() == null || request.getFrom() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (isInputNull(request)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getDeparture() == null
                || request.getArrival() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (isInputEmpty(request)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getFrom().getAirport().equals(request.getTo().getAirport())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tripService.findByJSON(request), HttpStatus.OK);
    }

    @GetMapping("/flights/{id}")
    public ResponseEntity<Trip> findTripById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(tripService.findById(id), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private boolean isInputEmpty(@RequestBody @Valid FindTripRequest request) {
        return request.getTo().getAirport().equals("")
                || request.getTo().getCity().equals("")
                || request.getTo().getCountry().equals("")
                || request.getFrom().getAirport().equals("")
                || request.getFrom().getCountry().equals("")
                || request.getFrom().getCity().equals("");
    }

    private boolean isInputNull(@RequestBody @Valid FindTripRequest request) {
        return request.getTo().getAirport() == null
                || request.getTo().getCountry() == null
                || request.getTo().getCity() == null
                || request.getFrom().getAirport() == null
                || request.getFrom().getCountry() == null
                || request.getFrom().getCity() == null;
    }
}

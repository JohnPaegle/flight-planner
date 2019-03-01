package io.codelex.flightplanner;

import io.codelex.flightplanner.api.AddTripsRequest;
import io.codelex.flightplanner.api.Airport;
import io.codelex.flightplanner.api.Trip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TripServiceTest {
    TripService tripService = new TripService();

    @BeforeEach
    void setup() {
        tripService.deleteAll();
    }

    @Test
    void should_not_find_anything_when_nothing_present() {
        // when
        List<Trip> result = tripService.search("Riga", "Stockholm");
        //then
        Assertions.assertTrue(result.isEmpty());
        //then
    }

    @Test
    void should_search_correctly_by_from() {
        //given
        Trip trip = tripService.addTrip(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RR"),
                new Airport("Estonia", "Tallin", "PP"),
                "Rayner",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        // when 
        List<Trip> trips = tripService.search("Riga", null);
        // then
        assertTrue(trips.contains(trip));
    }

    @Test
    void should_search_correctly_by_to() {
        //given
        Trip trip = tripService.addTrip(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RR"),
                new Airport("Estonia", "Tallin", "PP"),
                "Rayner",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        // when 
        List<Trip> trips = tripService.search(null, "Tallin");
        // then
        assertTrue(trips.contains(trip));
    }
    
    @Test
    void should_find_all_flights() {
        //given
        Trip trip1 = tripService.addTrip(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RR"),
                new Airport("Estonia", "Tallin", "PP"),
                "Rayner",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        Trip trip2 = tripService.addTrip(new AddTripsRequest(
                new Airport("France", "Paris", "BBW"),
                new Airport("Estonia", "Tallin", "PP"),
                "AirBaltic",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        Trip trip3 = tripService.addTrip(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RR"),
                new Airport("Germany", "Berlin", "TTT"),
                "VikinLine",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        //when
        List<Trip> trips = new ArrayList<>();
        trips.add(trip1);
        trips.add(trip2);
        trips.add(trip3);
        //then
        assertEquals(tripService.findAll(), trips);
    }


    @Test
    void should_able_to_add_a_flight() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertEquals(request.getFrom(), trip.getFrom());
        assertEquals(request.getTo(), trip.getTo() );
        assertEquals(request.getArrivalTime(), trip.getArrivalTime());
        assertEquals(request.getCarrier(), trip.getCarrier());
        assertEquals(request.getDepartureTime(), trip.getDepartureTime());
    }
    
    @Test
    void should_increment_id_when_adding_new_flight() {
        //given
        AddTripsRequest request = getAddTripRequest();
        AddTripsRequest request1 = new AddTripsRequest(
                new Airport("sss", "aaa", "fff"),
                new Airport("aa", "sssdf", "AfffRN"),
                "rew",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        //when
        Trip trip = tripService.addTrip(request);
        Trip trip1 = tripService.addTrip(request1);

        //then
        assertEquals(trip.getId() + 1, trip1.getId());
    }

    @Test
    void should_be_able_to_get_added_flight_by_id() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        comparing(request, trip);
    }

    @Test
    void should_get_no_result_if_no_ID_match_found() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        Assertions.assertThrows(NoSuchElementException.class,
                () -> tripService.findById(trip.getId() + 999));
    }

    @Test
    void should_not_be_able_to_add_duplicated_flight() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        tripService.addTrip(request);
        //then
        Assertions.assertThrows(IllegalStateException.class, () -> tripService.addTrip(request));
    }

    @Test
    void should_be_able_to_delete_flight_by_id() {
        AddTripsRequest request = new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Rynair",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
        );
        //when
        Trip trip = tripService.addTrip(request);
        tripService.deleteById(trip.getId());
        //then
        Assertions.assertEquals(tripService.findAll().size(), 0);
    }

    @Test
    void should_be_able_to_delete_all_flights() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        tripService.addTrip(request);
        tripService.deleteAll();
        //then
        assertEquals(tripService.findAll(), new ArrayList<>());
    }
    
    @Test
    void should_not_be_able_to_search_when_no_values_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        tripService.addTrip(request);
        //then
        assertEquals(tripService.search(null, null), (new ArrayList<>()));
    }

    @Test
    void should_find_flight_where_full_airport_name_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("RR", null).contains(trip));
    }

    @Test
    void should_find_flight_where_full_country_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Latvia", null).contains(trip));
    }

    @Test
    void should_find_flight_where_full_city_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Riga", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_airport_name_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        assertTrue(tripService.search("R", null).contains(trip));

    }

    @Test
    void should_find_flight_where_partial_country_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Latv", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_city_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Ri", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_lowercase_airport_name_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("r", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_uppercase_country_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("LATV", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_uppercase_city_from_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("RI", null).contains(trip));
    }

    @Test
    void should_find_flight_where_airport_name_from_with_space_at_the_end_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("RR ", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_country_from_with_space_at_the_end_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Latv ", null).contains(trip));
    }

    @Test
    void should_find_flight_where_partial_city_from_with_space_at_the_end_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Rig ", null).contains(trip));
    }

    @Test
    void should_find_trip_where_city_from_and_city_to_is_passed() {
        //given
        AddTripsRequest request = getAddTripRequest();
        //when
        Trip trip = tripService.addTrip(request);
        //then
        assertTrue(tripService.search("Latvia ", null).contains(trip));
    }

    private AddTripsRequest getAddTripRequest() {
        return new AddTripsRequest(
                new Airport("Latvia", "Riga", "RR"),
                new Airport("Estonia", "Tallin", "PP"),
                "Rayner",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private void comparing(AddTripsRequest request, Trip trip) {
        assertEquals(trip.getFrom(), request.getFrom());
        assertEquals(trip.getTo(), request.getTo());
        assertEquals(trip.getCarrier(), request.getCarrier());
        assertEquals(trip.getArrivalTime(), request.getArrivalTime());
        assertEquals(trip.getDepartureTime(), request.getDepartureTime());
    }
}

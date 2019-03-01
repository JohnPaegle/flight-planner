package io.codelex.flightplanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.codelex.flightplanner.api.AddTripsRequest;
import io.codelex.flightplanner.api.Airport;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(InternalTripsController.class)
class InternalTripsControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        javaTimeModule.addDeserializer(
                LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        javaTimeModule.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        javaTimeModule.addSerializer(
                LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        builder.modules(javaTimeModule);
        builder.featuresToDisable(WRITE_DATES_AS_TIMESTAMPS);

        MAPPER.registerModule(javaTimeModule);
    }

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TripService tripService;
    

    @Test
    void should_get_409_when_adding_a_the_duplicate() throws Exception {
        //given
        AddTripsRequest request = createdAddTripRequest();
        String json = MAPPER.writeValueAsString(request);
        Mockito.lenient()
                .when(tripService.isTripPresent(any()))
                .thenReturn(true);
        //expect
        mockMvc.perform(
                MockMvcRequestBuilders.put("/internal-api/flights")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void should_get_400_when_adding_a_trip_with_incorrect_input() throws Exception {
        //given
        List<AddTripsRequest> tripRequestList = new ArrayList<>();
        tripRequestList.add(new AddTripsRequest(
                null,
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                null,
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                null,
                LocalDateTime.now().plusDays(1L)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                null));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now()));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(1)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)));

        tripRequestList.add(new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                " ",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L)));


        for (AddTripsRequest addTripRequest : tripRequestList) {

            String json = MAPPER.writeValueAsString(addTripRequest);
            //expect
            mockMvc.perform(
                    MockMvcRequestBuilders.put("/internal-api/flights")
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    void should_get_404_when_finding_a_trip_by_id() throws Exception {
        //given
        Mockito.lenient()
                .when(tripService.findById(any()))
                .thenThrow(new NoSuchElementException());
        //expect
        mockMvc.perform(
                MockMvcRequestBuilders.get("/internal-api/flights/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void should_get_200_when_deleting_a_trip_by_id() throws Exception {
        //expect
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/internal-api/flights/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void should_get_400_when_deleting_a_trip_by_id() throws Exception {
        //given
        Mockito.doThrow(new NoSuchElementException()).when(tripService).deleteById(any());
        //expect
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/internal-api/flights/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
    private AddTripsRequest createdAddTripRequest() {
        return new AddTripsRequest(
                new Airport("Latvia", "Riga", "RIX"),
                new Airport("Sweden", "Stockholm", "ARN"),
                "Air Baltic",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L));
    }
}
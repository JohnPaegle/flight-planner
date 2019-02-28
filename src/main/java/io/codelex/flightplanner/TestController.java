package io.codelex.flightplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("testing-api/")
public class TestController {
    @Autowired
    private TripService tripService;

    @PostMapping("clear")
    public void clearAll() {
        tripService.deleteAll();
    }
}

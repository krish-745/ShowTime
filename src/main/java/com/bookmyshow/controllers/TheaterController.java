package com.bookmyshow.controllers;

import com.bookmyshow.models.Theater;
import com.bookmyshow.services.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theaters")
public class TheaterController {
    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public List<Theater> getAllTheaters() {
        return theaterService.getAllTheaters();
    }

    @PostMapping
    public ResponseEntity<Theater> createTheater(@RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.addTheater(theater));
    }
}

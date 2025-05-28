package com.bookmyshow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bookmyshow.models.Seat;
import com.bookmyshow.models.Show;
import com.bookmyshow.services.SeatGraphService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/seats")
public class SeatController {
    @Autowired
    private SeatGraphService seatGraphService;

    @GetMapping("/available/{showId}/{numSeats}")
    public List<Seat> findAdjacentSeats(@PathVariable int showId, @PathVariable int numSeats) {
        return seatGraphService.findAdjacentSeats(showId, numSeats);
    }

    @PostMapping("/book")
    public String bookSeats(@RequestBody List<Long> seatIds) {
        boolean success = seatGraphService.bookSeats(seatIds);
        return success ? "Seats booked successfully" : "Some seats are already booked!";
    }
}

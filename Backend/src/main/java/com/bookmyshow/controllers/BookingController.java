package com.bookmyshow.controllers;

import com.bookmyshow.dto.BookingDTO;
import com.bookmyshow.dto.BookingRequest;
import com.bookmyshow.models.Booking;
import com.bookmyshow.repositories.BookingRepository;
import com.bookmyshow.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bookings);
    }
    
    @PostMapping
    public ResponseEntity<Booking> bookTickets(@RequestBody BookingRequest request) {
        Booking booking = bookingService.bookSeats(request.getShowId(), request.getSeatIds(), request.getUserId());
        return ResponseEntity.ok(booking);
    }
}

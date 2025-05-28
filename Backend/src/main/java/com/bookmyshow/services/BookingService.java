package com.bookmyshow.services;

import com.bookmyshow.ds.BookingHistory;
import com.bookmyshow.dto.BookingDTO;
import com.bookmyshow.dto.ShowDTO;
import com.bookmyshow.mapper.BookingMapper;
import com.bookmyshow.models.*;
import com.bookmyshow.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookingHistory bookingHistory;

    public List<BookingDTO> getAllBookings() {
        if (bookingHistory.historySize() == 0) {
            List<Booking> list = bookingRepository.findAll();
            list.stream().forEach(b -> {
                bookingHistory.addBooking(BookingMapper.mapBookingToBookingDTO(b));
            });
        }
        return bookingHistory.getAllBookingHistory();
    }

    private void addBookingForFastFetch(Booking booking) {
        BookingDTO bookingDTO = BookingMapper.mapBookingToBookingDTO(booking);
        bookingHistory.addBooking(bookingDTO);
    }

    @Transactional
    public Booking bookSeats(Long showId, List<Long> seatIds, Long userId) {
        List<Seat> seatList = seatRepository.findAllById(seatIds);

        if (!areSeatsAvailable(seatList)) {
            throw new RuntimeException("One or more seats are not available.");
        }

        Show show = showRepository.findById(showId).get();

        int remainingSeats = getRemainingSeats(showId);

        BigDecimal totalPrice = calculateTotalPrice(showId, seatList, show.getShowTime(), remainingSeats);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = new Booking();
        booking.setShow(show);
        booking.setSeats(seatList);
        booking.setUser(user);
        booking.setTotalPrice(totalPrice);
        Booking savedBooking = bookingRepository.save(booking);

        markSeatsAsBooked(seatList);

        addBookingForFastFetch(booking);
        bookingHistory.addBooking(BookingMapper.mapBookingToBookingDTO(booking));
        return savedBooking;
    }

    private int getRemainingSeats(Long showId) {
        /*int totalSeats = seatRepository.countSeatsByShowId(showId);
        int bookedSeats = seatRepository.countSeatsByShowIdAndIsBooked(showId);*/
        int totalSeats = seatRepository.findByShowId(showId).size();
        int bookedSeats = seatRepository.findByShowIdAndIsBookedTrue(showId).size();
        return totalSeats - bookedSeats;
    }

    private boolean areSeatsAvailable(List<Seat> seatList) {
        return seatList.stream().allMatch(seat -> seat.isBooked() == false);
    }

    private BigDecimal calculateTotalPrice(Long showId, List<Seat> seatList, LocalDateTime showTime, int remainingSeats) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Seat seat : seatList) {
            Optional<Pricing> pricingOptional = pricingRepository.findByShowIdAndSeatCategory(showId, seat.getSeatCategory());

            if (pricingOptional.isPresent()) {
                Pricing pricing = pricingOptional.get();
                BigDecimal seatPrice = pricingService.calculatePrice(pricing.getBasePrice(), remainingSeats, showTime);
                totalPrice = totalPrice.add(seatPrice);
            } else {
                throw new RuntimeException("Pricing not found for showId: " + showId + " and seatCategory: " + seat.getSeatCategory());
            }
        }
        return totalPrice;
    }

    private void markSeatsAsBooked(List<Seat> seatList) {
        seatList.forEach(seat -> {
            seat.bookSeat();
        });
        seatRepository.saveAll(seatList);
    }
}

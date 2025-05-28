package com.bookmyshow.controllers;

import com.bookmyshow.dto.ShowDTO;
import com.bookmyshow.dto.SeatDTO;
import com.bookmyshow.mapper.BookingMapper;
import com.bookmyshow.models.Movie;
import com.bookmyshow.models.Show;
import com.bookmyshow.services.PricingService;
import com.bookmyshow.services.ShowService;
import com.bookmyshow.models.Theater;
import com.bookmyshow.repositories.MovieRepository;
import com.bookmyshow.repositories.SeatRepository;
import com.bookmyshow.repositories.ShowRepository;
import com.bookmyshow.repositories.TheaterRepository;
import com.bookmyshow.dto.ShowRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/shows")
public class ShowController {

    private final ShowRepository showRepository;
    @Autowired
    private final ShowService showService;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;
    private final PricingService pricingService;

    public ShowController(ShowRepository showRepository, MovieRepository movieRepository, TheaterRepository theaterRepository, ShowService showService, SeatRepository seatRepository, PricingService pricingService) {
        this.showRepository = showRepository;
		this.showService = showService;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.seatRepository = seatRepository;
        this.pricingService = pricingService;
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }
    
    @GetMapping("/price/{showId}")
    public ResponseEntity<Map<String, BigDecimal>> getDynamicPrices(@PathVariable Long showId) {
        return ResponseEntity.ok(pricingService.fetchCurrentPricingForShow(showId));
    }


    
//    @GetMapping("/{showId}")
//    public ResponseEntity<Show> getShowById(@PathVariable Long showId) {
//        return showService.getShowById(showId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
    
    @GetMapping("/{movieId}")
    public ResponseEntity<List<ShowDTO>> getShowsByMovieId(@PathVariable Long movieId) {
        List<Show> shows = showRepository.findByMovie_MovieId(movieId);

        List<ShowDTO> showDTOs = shows.stream().map(show -> {
            ShowDTO showDTO = new ShowDTO();
            showDTO.setId(show.getId());
            showDTO.setMovie(BookingMapper.mapMovieToMovieDTO(show.getMovie()));  // Convert Movie
            showDTO.setTheater(BookingMapper.mapTheatreToTheatreDTO(show.getTheater()));  // Convert Theater
            showDTO.setShowTime(show.getShowTime());

            // Convert seats to SeatDTO list
            List<SeatDTO> seatDTOs = show.getSeats().stream().map(seat -> 
                new SeatDTO(seat.getId(), seat.getRowNumber(), seat.getSeatNumberInRow(), seat.isBooked())
            ).toList();

            showDTO.setSeats(seatDTOs);
            return showDTO;
        }).toList();

        return ResponseEntity.ok(showDTOs);
    }

    @GetMapping("/seats/{showId}")
    public ResponseEntity<?> getSeatsByShowId(@PathVariable Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<SeatDTO> bookedSeats = show.getSeats().stream()
                .filter(seat -> seat.isBooked())
                .map(seat -> new SeatDTO(seat.getId(), seat.getRowNumber(), seat.getSeatNumberInRow(), true))
                .toList();

        List<SeatDTO> unbookedSeats = show.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .map(seat -> new SeatDTO(seat.getId(), seat.getRowNumber(), seat.getSeatNumberInRow(), false))
                .toList();

        return ResponseEntity.ok(new SeatsResponse(bookedSeats, unbookedSeats));
    }

    public record SeatsResponse(List<SeatDTO> bookedSeats, List<SeatDTO> unbookedSeats) {}
    
    @PostMapping
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        //Convert String to LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime showTime = LocalDateTime.parse(request.getShowTime(), formatter);

        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowTime(showTime);

        Show savedShow = showRepository.save(show);
        return ResponseEntity.ok(BookingMapper.mapShowToShowDTO(show));
    }

}
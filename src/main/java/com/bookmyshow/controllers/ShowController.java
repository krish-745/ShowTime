package com.bookmyshow.controllers;

import com.bookmyshow.dto.ShowDTO;
import com.bookmyshow.mapper.BookingMapper;
import com.bookmyshow.models.Movie;
import com.bookmyshow.models.Show;
import com.bookmyshow.services.ShowService;
import com.bookmyshow.models.Theater;
import com.bookmyshow.repositories.MovieRepository;
import com.bookmyshow.repositories.ShowRepository;
import com.bookmyshow.repositories.TheaterRepository;
import com.bookmyshow.dto.ShowRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    private final ShowRepository showRepository;
    @Autowired
    private final ShowService showService;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    public ShowController(ShowRepository showRepository, MovieRepository movieRepository, TheaterRepository theaterRepository, ShowService showService) {
        this.showRepository = showRepository;
		this.showService = showService;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }
    
    @GetMapping("/{showId}")
    public ResponseEntity<Show> getShowById(@PathVariable Long showId) {
        return showService.getShowById(showId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
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

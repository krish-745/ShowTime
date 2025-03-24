package com.bookmyshow.controllers;

import com.bookmyshow.models.Movie;
import com.bookmyshow.services.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieService.addMovie(movie);
    }

    @GetMapping("/search/{searchText}")
    public List<String> searchMovies(@PathVariable String searchText) {
        return movieService.searchMovies(searchText);
    }
}

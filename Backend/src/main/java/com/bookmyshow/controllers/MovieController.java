package com.bookmyshow.controllers;

import com.bookmyshow.models.Movie;
import com.bookmyshow.services.MovieService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return movieService.getAllMovies();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }
    
    @GetMapping("/genre/{movieId}")
    public List<Movie> getRecommendedMovies(@PathVariable Long movieId) {
        return movieService.getRecommendedMovies(movieId);
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
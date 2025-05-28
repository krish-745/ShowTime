package com.bookmyshow.services;

import com.bookmyshow.ds.Trie;
import com.bookmyshow.models.Movie;
import com.bookmyshow.models.Theater;
import com.bookmyshow.repositories.MovieRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    @Qualifier("moviesFastSearch")
    private Trie movieSearch;
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        updateMoviesFastSearch();
        return savedMovie;
    }

    public List<String> searchMovies(String searchText) {
        if(StringUtils.isNotBlank(searchText)) {
            return movieSearch.getWordsStartingWith(searchText.toLowerCase());
        }
        return new ArrayList<>();
    }

    public void updateMoviesFastSearch() {
        List<Movie> movieList = movieRepository.findAll();
        movieSearch.clear();
        movieList.stream().forEach(m -> {
            movieSearch.insert(m.getName().toLowerCase());
        });
    }
    
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }
    
    public List<Movie> getRecommendedMovies(Long movieId) {
        // Get the movie by ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        // Get the genres of the movie
        List<String> movieGenres = Arrays.asList(movie.getGenre().split(", "));

        // Get all movies except the given one & filter by shared genres
        return movieRepository.findAll().stream()
            .filter(m -> !m.getMovieId().equals(movieId)) // Exclude the given movie
            .filter(m -> {
                List<String> mGenres = Arrays.asList(m.getGenre().split(", "));
                return mGenres.stream().anyMatch(movieGenres::contains); // Check if any genre matches
            })
            .collect(Collectors.toList());
    }


}
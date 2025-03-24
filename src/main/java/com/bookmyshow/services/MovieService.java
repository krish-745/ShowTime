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
import java.util.List;

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

}

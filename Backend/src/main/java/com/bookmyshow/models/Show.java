package com.bookmyshow.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shows")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;
    
    private LocalDateTime showTime;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    public Show() {}

    public Show(Movie movie, Theater theater, LocalDateTime showTime, List<Seat> seats) {
        this.movie = movie;
        this.theater = theater;
        this.showTime = showTime;
        this.seats = seats;
    }
    
    public Show(Movie movie, Theater theater, LocalDateTime showTime) {
        this.movie = movie;
        this.theater = theater;
        this.showTime = showTime;
    }
    
    public Long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }
    
    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }
    
    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}
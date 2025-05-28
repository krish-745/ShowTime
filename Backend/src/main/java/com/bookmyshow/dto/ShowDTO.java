package com.bookmyshow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ShowDTO {

    private Long id;

    private MovieDTO movie;

    private TheaterDTO theater;

    private LocalDateTime showTime;
    
    private List<SeatDTO> seats;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovieDTO getMovie() {
        return movie;
    }

    public void setMovie(MovieDTO movie) {
        this.movie = movie;
    }

    public TheaterDTO getTheater() {
        return theater;
    }

    public void setTheater(TheaterDTO theater) {
        this.theater = theater;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

	public List<SeatDTO> getSeats() {
		return seats;
	}

	public void setSeats(List<SeatDTO> seats) {
		this.seats = seats;
	}

}
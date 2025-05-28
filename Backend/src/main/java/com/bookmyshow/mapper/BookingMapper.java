package com.bookmyshow.mapper;

import com.bookmyshow.dto.*;
import com.bookmyshow.models.*;

import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDTO mapBookingToBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setShow(mapShowToShowDTO(booking.getShow()));
        bookingDTO.setSeats(booking.getSeats().stream().map( s -> mapSeatToSeatDTO(s)).collect(Collectors.toList()));
        bookingDTO.setTotalPrice(booking.getTotalPrice());
        return bookingDTO;
    }


    public static ShowDTO mapShowToShowDTO(Show show) {
        ShowDTO showDTO = new ShowDTO();
        showDTO.setId(show.getId());
        showDTO.setMovie(mapMovieToMovieDTO(show.getMovie()));
        showDTO.setShowTime(show.getShowTime());
        showDTO.setTheater(mapTheatreToTheatreDTO(show.getTheater()));
        return showDTO;
    }

    public static TheaterDTO mapTheatreToTheatreDTO(Theater theater) {
        TheaterDTO theaterDTO = new TheaterDTO();
        theaterDTO.setTheaterId(theater.getTheater_id());
        theaterDTO.setName(theater.getName());
        theaterDTO.setLocation(theater.getLocation());
        return theaterDTO;
    }

    public static MovieDTO mapMovieToMovieDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setMovieId(movie.getMovieId());
        movieDTO.setName(movie.getName());
        movieDTO.setOverview(movie.getOverview());
        movieDTO.setDuration(movie.getDuration());
        movieDTO.setGenre(movie.getGenre());
        movieDTO.setRating(movie.getRating());
        movieDTO.setPosterPath(movie.getPosterPath());
        return movieDTO;
    }


    private static SeatDTO mapSeatToSeatDTO(Seat seat) {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setId(seat.getId());
        seatDTO.setSeatNumberInRow(seat.getSeatNumberInRow());
        seatDTO.setBooked(seat.isBooked());
        seatDTO.setRowNumber(seat.getRowNumber());
        return seatDTO;
    }

}
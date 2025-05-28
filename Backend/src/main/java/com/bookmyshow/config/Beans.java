package com.bookmyshow.config;

import com.bookmyshow.ds.BookingHistory;
import com.bookmyshow.ds.Trie;
import com.bookmyshow.dto.BookingDTO;
import com.bookmyshow.dto.ShowDTO;
import com.bookmyshow.models.Booking;
import com.bookmyshow.models.Seat;
import com.bookmyshow.models.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Beans {

    @Bean
    @Qualifier("moviesFastSearch")
    public Trie buildFastSearchForMovies() {
        return new Trie();
    }

    @Bean
    @Qualifier("theaterFastSearch")
    public Trie buildFastSearchForTheatre() {
        return new Trie();
    }

    @Bean
    @Qualifier("userMap")
    public ConcurrentHashMap<Long, User> buildQuickAccessToUser() {
        return new ConcurrentHashMap();
    }

    @Bean
    @Qualifier("bookingHistory")
    public BookingHistory buildBookingHistory() {
        return new BookingHistory();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

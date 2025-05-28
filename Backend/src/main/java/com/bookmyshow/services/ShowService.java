package com.bookmyshow.services;

import com.bookmyshow.dto.ShowDTO;
import com.bookmyshow.models.Show;
import com.bookmyshow.repositories.ShowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {
	@Autowired
    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Show addShow(Show show) {
        return showRepository.save(show);
    }
    
    public Optional<Show> getShowById(Long showId) {
        return showRepository.findById(showId);
    }
}
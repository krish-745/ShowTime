package com.bookmyshow.services;

import com.bookmyshow.ds.Trie;
import com.bookmyshow.models.Theater;
import com.bookmyshow.repositories.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterService {
    private final TheaterRepository theaterRepository;

    @Autowired
    @Qualifier("theaterFastSearch")
    private Trie theaterFastSearch;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Theater addTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    public void updateTheatreFastSearch() {
        List<Theater> theatreList = theaterRepository.findAll();
        theaterFastSearch.clear();
        theatreList.stream().forEach(m -> {
            theaterFastSearch.insert(m.getName().toLowerCase());
        });
    }
}

package com.bookmyshow.repositories;

import com.bookmyshow.models.Show;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
	List<Show> findByMovie_MovieId(Long movieId);
}
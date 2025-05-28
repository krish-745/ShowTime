package com.bookmyshow.repositories;

import com.bookmyshow.models.Seat;
import com.bookmyshow.models.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowIdAndIsBookedFalse(long showId);

    List<Seat> findByShowIdAndIsBookedTrue(long showId);
    List<Seat> findByShowId(long showId);

  /*  @Query("SELECT count(s.id) FROM Seat s WHERE s.id = :showId")
    int countSeatsByShowId(@Param("showId") Long showId);

    @Query("SELECT count(s.id) FROM Seat s WHERE s.id = :showId AND s.isBooked = true")
    int countSeatsByShowIdAndIsBooked(@Param("showId") Long showId);*/
}

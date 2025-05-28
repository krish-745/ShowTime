package com.bookmyshow.repositories;

import com.bookmyshow.models.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {

    Optional<Pricing> findByShowIdAndSeatCategory(Long showId, String seatCategory);

	List<Pricing> findByShowId(long showId);
}

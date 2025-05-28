package com.bookmyshow.repositories;

import com.bookmyshow.models.PricingTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingTierRepository extends JpaRepository<PricingTier, Long> {
}

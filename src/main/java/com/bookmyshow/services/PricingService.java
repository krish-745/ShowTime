package com.bookmyshow.services;

import com.bookmyshow.models.PricingTier;
import com.bookmyshow.repositories.PricingTierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class PricingService {

    @Autowired
    private PricingTierRepository pricingTierRepository;

    public BigDecimal calculatePrice(BigDecimal basePrice, int remainingSeats, LocalDateTime showTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration timeUntilShow = Duration.between(now, showTime);

        PriorityQueue<PricingTier> pricingTierQueue = new PriorityQueue<>(
                Comparator.comparingInt(PricingTier::getSeatThreshold).thenComparing(PricingTier::getTimeThreshold));

        pricingTierQueue.addAll(pricingTierRepository.findAll());

        PricingTier applicableTier = pricingTierQueue.stream()
                .filter(tier -> remainingSeats <= tier.getSeatThreshold() && timeUntilShow.compareTo(tier.getTimeThreshold()) <= 0)
                .max(Comparator
                        .comparingInt(PricingTier::getSeatThreshold)
                        .thenComparing(PricingTier::getTimeThreshold))
                .orElse(new PricingTier(BigDecimal.ONE, Integer.MAX_VALUE, Duration.ofDays(365))); //Default to base price

        return basePrice.multiply(applicableTier.getDynamicFactor());
    }

    public PricingTier addPricingTier(PricingTier tier) {
        return pricingTierRepository.save(tier);
    }

}

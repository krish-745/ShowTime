package com.bookmyshow.services;

import com.bookmyshow.models.Pricing;
import com.bookmyshow.models.PricingTier;
import com.bookmyshow.models.Show;
import com.bookmyshow.repositories.PricingRepository;
import com.bookmyshow.repositories.PricingTierRepository;
import com.bookmyshow.repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PricingService {

    @Autowired
    private PricingTierRepository pricingTierRepository;

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowService showService;

    @Autowired
    private PricingService pricingService;

    public BigDecimal calculatePrice(BigDecimal basePrice, int remainingSeats, LocalDateTime showTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration timeUntilShow = Duration.between(now, showTime);

        PriorityQueue<PricingTier> pricingTierQueue = new PriorityQueue<>(
                Comparator.comparingInt(PricingTier::getSeatThreshold).thenComparing(PricingTier::getTimeThreshold));

        pricingTierQueue.addAll(pricingTierRepository.findAll());

        PricingTier applicableTier = pricingTierQueue.stream()
                .filter(tier -> remainingSeats <= tier.getSeatThreshold() || timeUntilShow.compareTo(tier.getTimeThreshold()) <= 0)
                .min(Comparator
                        .comparingInt(PricingTier::getSeatThreshold)
                        .thenComparing(PricingTier::getTimeThreshold))
                .orElse(new PricingTier(BigDecimal.ONE, Integer.MAX_VALUE, Duration.ofDays(365))); //Default to base price

        return basePrice.multiply(applicableTier.getDynamicFactor());
    }

    public PricingTier addPricingTier(PricingTier tier) {
        return pricingTierRepository.save(tier);
    }

    public Map<String, BigDecimal> fetchCurrentPricingForShow(long showId) {
        Map<String, BigDecimal> result = new HashMap<>();
        Optional<Show> showOptional = showService.getShowById(showId);
        Show show = showOptional.get();
        LocalDateTime showTime = show.getShowTime();
        int totalSeats = seatRepository.findByShowId(showId).size();
        int bookedSeats = seatRepository.findByShowIdAndIsBookedTrue(showId).size();
        int remainingSeats = totalSeats - bookedSeats;

        List<Pricing> pricingList = pricingRepository.findByShowId(showId);
        pricingList.stream().forEach(p -> {
            result.put(p.getSeatCategory(), pricingService.calculatePrice(p.getBasePrice(), remainingSeats, showTime));
        });
        return result;
    }

}

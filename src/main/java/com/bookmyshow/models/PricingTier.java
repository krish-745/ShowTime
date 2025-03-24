package com.bookmyshow.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "pricing_tier")
public class PricingTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dynamic_factor", nullable = false)
    private BigDecimal dynamicFactor;

    @Column(name = "seat_threshold", nullable = false)
    private int seatThreshold;

    @Column(name = "time_threshold", nullable = false)
    private Duration timeThreshold;

    // Constructors
    public PricingTier() {
    }

    public PricingTier(BigDecimal dynamicFactor, int seatThreshold, Duration timeThreshold) {
        this.dynamicFactor = dynamicFactor;
        this.seatThreshold = seatThreshold;
        this.timeThreshold = timeThreshold;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDynamicFactor() {
        return dynamicFactor;
    }

    public void setDynamicFactor(BigDecimal dynamicFactor) {
        this.dynamicFactor = dynamicFactor;
    }

    public int getSeatThreshold() {
        return seatThreshold;
    }

    public void setSeatThreshold(int seatThreshold) {
        this.seatThreshold = seatThreshold;
    }

    public Duration getTimeThreshold() {
        return timeThreshold;
    }

    public void setTimeThreshold(Duration timeThreshold) {
        this.timeThreshold = timeThreshold;
    }

    @Override
    public String toString() {
        return "PricingTier{" +
                "id=" + id +
                ", dynamicFactor=" + dynamicFactor +
                ", seatThreshold=" + seatThreshold +
                ", timeThreshold=" + timeThreshold +
                '}';
    }
}

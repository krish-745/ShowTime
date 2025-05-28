package com.bookmyshow.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "seat_category", nullable = false)
    private String seatCategory;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    public Pricing() {}

    public Pricing(Long showId, String seatCategory, BigDecimal basePrice, BigDecimal dynamicFactor, LocalDateTime effectiveStartTime, LocalDateTime effectiveEndTime) {
        this.showId = showId;
        this.seatCategory = seatCategory;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public String getSeatCategory() {
        return seatCategory;
    }

    public void setSeatCategory(String seatCategory) {
        this.seatCategory = seatCategory;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "id=" + id +
                ", showId=" + showId +
                ", seatCategory='" + seatCategory + '\'' +
                ", basePrice=" + basePrice +
                '}';
    }
}
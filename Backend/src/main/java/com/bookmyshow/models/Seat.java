package com.bookmyshow.models;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rowNumber;
    private int seatNumberInRow;
    private boolean isBooked;
    private String seatCategory;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = true)
    private Show show;

    public Seat() {}

    public Seat(int rowNumber, int colNumber, Show show) {
        this.rowNumber = rowNumber;
        this.seatNumberInRow = colNumber;
        this.isBooked = false;
        this.show = show;
    }
    
    public Seat(int rowNumber, int colNumber, Show show, boolean isBooked) {
        this.rowNumber = rowNumber;
        this.seatNumberInRow = colNumber;
        this.isBooked = isBooked;
        this.show = show;
    }
    
    public Seat(int rowNumber, int colNumber) {
        this.rowNumber = rowNumber;
        this.seatNumberInRow = colNumber;
        this.isBooked = false;
    }
    
    public Seat(Show show) {
    	this.show = show;
    }

    public Long getId() {
        return id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumberInRow() {
        return seatNumberInRow;
    }

    public void setSeatNumberInRow(int seatNumberInRow) {
        this.seatNumberInRow = seatNumberInRow;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void bookSeat() {
        this.isBooked = true;
    }

    public String getSeatCategory() {
        return seatCategory;
    }

    public void setSeatCategory(String seatCategory) {
        this.seatCategory = seatCategory;
    }
}

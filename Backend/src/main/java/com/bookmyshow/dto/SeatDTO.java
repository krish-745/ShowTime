package com.bookmyshow.dto;

public class SeatDTO {

    private Long id;
    private int rowNumber;
    private int seatNumberInRow;
    private boolean isBooked;
    private String seatCategory;

    public SeatDTO(Long id, int rowNumber, int seatNumberInRow, boolean isBooked) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.seatNumberInRow = seatNumberInRow;
        this.isBooked = isBooked;
    }
    
    public SeatDTO() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
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

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getSeatCategory() {
        return seatCategory;
    }

    public void setSeatCategory(String seatCategory) {
        this.seatCategory = seatCategory;
    }
}
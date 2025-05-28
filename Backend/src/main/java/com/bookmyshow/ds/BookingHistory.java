package com.bookmyshow.ds;

import com.bookmyshow.dto.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BookingHistory {

    private LinkedList<BookingDTO> history;

    public BookingHistory() {
        this.history = new LinkedList<>();
    }

    public void addBooking(BookingDTO booking) {
        history.addLast(booking);
    }

    public List<BookingDTO> getAllBookingHistory() {
        if (history.isEmpty()) {
            System.out.println("Booking history is empty.");
            return new ArrayList<>();
        }
        return history;
    }

    public BookingDTO getBooking(int index) {
        if (index < 1 || index > history.size()) {
            return null;
        }
        return history.get(index - 1);
    }

    public int historySize() {
        return history.size();
    }
}

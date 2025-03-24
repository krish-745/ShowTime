package com.bookmyshow.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal totalPrice;
    
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
        )
        private List<Seat> seats;


    public Booking(User user1, Show show1, List<Seat> seat1) {    	
    	user=user1;
 		show=show1;
 		seats=seat1;
	}

	public Booking() {
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}

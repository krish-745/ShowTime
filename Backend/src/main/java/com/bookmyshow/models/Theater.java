package com.bookmyshow.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long theater_id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String location;
	public Theater(String name1, String loc1) {
		name=name1;
		location=loc1;
	}
	public Theater() {
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Long getTheater_id() {
		return theater_id;
	}

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<Show> shows;
}

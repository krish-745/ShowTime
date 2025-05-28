package com.bookmyshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class BookmyshowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmyshowApplication.class, args);
	}

}

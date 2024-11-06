package com.eska.evenity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EvenityApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvenityApplication.class, args);
	}

}

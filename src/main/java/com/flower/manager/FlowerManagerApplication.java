package com.flower.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowerManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowerManagerApplication.class, args);
	}

}

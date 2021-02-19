package fr.bks.pokerPlanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PokerPlanningApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokerPlanningApplication.class, args);
	}
}

package br.com.neogrid.challenge.core.storage;

import java.time.LocalTime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties referring to the assembly line periods.
 * @author Divino Martins
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties("challenge.period")
public class PeriodProperties {
	
	private Afternoon afternoon = new Afternoon();
	private Morning morning = new Morning();
		
	@Getter
	@Setter
	public class Afternoon {
		
		/**
		 * The singleton instance for the begin time of the afternoon of the assembly line.
		 */
		private LocalTime begin;
		
		/**
		 * The singleton instance for the start time of the morning of the assembly line.
		 * Constraints: The labor gymnastics activities can start no earlier than 4:00 pm and no later than 5:00 pm.
		 */
		private LocalTime laborGymMin;
		
		/**
		 * The singleton instance of the time limit for starting labor gymnastics.
		 * Constraints: The labor gymnastics activities can start no earlier than 4:00 pm and no later than 5:00 pm.
		 */
		private LocalTime laborGymMax;
		
	}

	@Getter
	@Setter
	public class Morning {
		
		/**
		 * The singleton instance for the begin time of the morning of the assembly line.
		 * Constraints: The morning period begins at 9:00 am and must finish by 12:00 noon, for lunch.
		 */
		private LocalTime begin;
		
		/**
		 * The singleton instance for the finish time of the morning of the assembly line.
		 * Constraints: The morning period begins at 9:00 am and must finish by 12:00 noon, for lunch.
		 */
		private LocalTime finish;
		
	}
}

package br.com.neogrid.challenge.domain.model;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Step {

	private String title;
	private Integer timeInMinutes;
	private LocalTime time;
	
}

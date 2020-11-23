package br.com.neogrid.challenge.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AssemblyLine {

	private Integer number;
	private List<Step> steps = new ArrayList<>();
	
	public String getDescription() {
		return String.format("Linha de montagem %d", number);
	}
	
}

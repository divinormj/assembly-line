package br.com.neogrid.challenge.domain.model;

import java.util.List;

import br.com.neogrid.challenge.domain.service.FileStorageService.StreamStorage;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Response {
	
	private StreamStorage input;
	private StreamStorage output;
	private List<String> titles;
	private List<AssemblyLine> assemblyLines;
	
}
